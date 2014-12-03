
import numpy as np
import scipy.signal as signal
import wave,time, threading, os
import yaml,struct
import Queue,re,sys#,multiprocessing

current_directory = os.path.dirname(os.path.abspath(__file__))
main_path = os.path.dirname(os.path.dirname(current_directory))
main_path = main_path.replace('\\','/')
data_path = main_path + "/FITII/Auana-N/"



#How many data in fft process 
DEFAULT_FFT_SIZE = 4096
#sub-fingerprint depth
FIN_BIT = 32
mel = (2596*np.log10(1+3000/700))/float(FIN_BIT+1)
table = {}
queue = Queue.Queue()


def hamming_weight(x):
	m1  = 0x5555555555555555 #binary: 0101...  
	m2  = 0x3333333333333333 #binary: 00110011..  
	m4  = 0x0f0f0f0f0f0f0f0f #binary:  4 zeros,  4 ones ...  

	x -= (x >> 1) & m1             #put count of each 2 bits into those 2 bits  
	x = (x & m2) + ((x >> 2) & m2) #put count of each 4 bits into those 4 bits   
	x = (x + (x >> 4)) & m4        #put count of each 8 bits into those 8 bits   
	x += x >>  8                   #put count of each 16 bits into their lowest 8 bits  
	x += x >> 16                   #put count of each 32 bits into their lowest 8 bits  
	x += x >> 32                   #put count of each 64 bits into their lowest 8 bits  
	return x & 0x7f

def detect_broken_frame(data, channel):
	FLAG = 0
	DETECT_WIN = 256
	VAR_THRESHOLD = 2000
	bf = []
	var0 = var1 = 0
	for i in  xrange(len(data)/DETECT_WIN):
		var = int(np.var(data[i*DETECT_WIN:(i+1)*DETECT_WIN]))
		if i>1:
			distance0 = abs(var-var0)
			distance1 = abs(var-var1)
			if FLAG == 0:
				if (distance0 >VAR_THRESHOLD) and (distance1 > VAR_THRESHOLD) and (var < 90) :
					FLAG = 1
			elif FLAG == 1:
				if (distance0 >VAR_THRESHOLD) and (distance1 > VAR_THRESHOLD) and (var0 < 90):
					FLAG = 0
					bf.append(round(((i+1) * DETECT_WIN)/float(44100),3))
				elif i%86 == 0:
					FLAG = 0
		var0,var1=var1,var

	if len(bf)== 0:
		return 0
	else:
		return bf
#######################################################
#    search function                                                                                                    
#######################################################
def find_match(sdata, tdata, window_size, framerate):
	confidence = 0
	next_begain = 0

	tlen = len(tdata)
	slen = len(sdata)
	step = 1

	#Arithmetic sequence tolerance uplimit and down limit
	up_limit = window_size+2
	dw_limit = window_size-2

	for a in  xrange(0,tlen/window_size,step):
		dismin = 0xffffffff
		min_seq= None

		for index in  xrange(next_begain, slen - window_size, step):
			#calculate the distant of each subfingerprint between two songs in a search-window
			D = (tdata[(a*window_size) : (a+1)*window_size]) ^ (sdata[index : index+window_size])
			#get distant of two search-windows
			dis = 0
			for d in D:
				dis += hamming_weight(long(d))
			if dis < dismin:
				dismin  = dis
				min_seq = index

		if a>0 and dw_limit<=(min_seq-min_seq0)<=up_limit:confidence += 1
		if a>13 and confidence<4:break
		if confidence>=6: next_begain=min_seq
		min_seq0 = min_seq

	return round(float(confidence)/(tlen/(step*window_size)-2), 3)

def recognize(tdata,channels,framerate):
	if len(tdata) < 200:
		DEFAULT_SEARCH_WIN = 2
	else:
		DEFAULT_SEARCH_WIN = 20
	
	match_audio = None
	max_confidence = 0

	source_data = open(data_path+'AudioFingerData.yml','r')
	sdata = yaml.load(source_data)
	for key in sdata:
		confidence = find_match(np.array(sdata[key][str(channels)],dtype = np.uint32), tdata, DEFAULT_SEARCH_WIN, framerate)
		if confidence >= 0.5:
			source_data.close()
			return key,confidence
		if confidence > max_confidence:
			match_audio = key
			max_confidence = confidence
	source_data.close()
	if match_audio == None:
		return None,0
	else:
		return match_audio, max_confidence

###########################################################
#   Get audio fingerprint
###########################################################
def fft_transformation(data,scale):
	#hanning window to smooth the edge
	xs = np.multiply(data, signal.hann(DEFAULT_FFT_SIZE, sym=0))
	#fft transfer
	xf = np.abs(np.fft.rfft(xs))
	#200HZ ~ 2000HZ, 56 * 32Hz(size) , scale: 44100/16384 = 2.7
	xfp = 20*np.log10(np.clip(xf, 1e-20, 1e100))
	
	sub_fin = 0L
	sumdb=0
	num=0
	for n in  xrange(1,FIN_BIT+1):
		p1 = 0
		p2 = 0
		if len(table)< 32:
			b0 = 700*(10**((n-1)*mel/2596)-1)
			b1 = 700*(10**((n+1)*mel/2596)-1)
			table.update({n:[b0,b1]})
		else:
			b0 = table[n][0]
			b1 = table[n][1]
		for i in  xrange(int(b0),int((b1+1))):
			fp = xfp[int(i/scale)]
			p1 += fp*i
			p2 += fp
			num += 1
		sumdb += p2
		if (p1/p2-(b0+b1)/2)/(b1-b0) >= 0:
			sub_fin = sub_fin | (1<<(n-1))

	return sub_fin,sumdb/num

def get_finger(data,framerate):
	global DEFAULT_OVERLAP
	#Overlap frmate depth
	if len(data)<220500:#(5s)
		DEFAULT_OVERLAP = 8
	else:
		DEFAULT_OVERLAP = 4

	scale = framerate/float(DEFAULT_FFT_SIZE)
	st = 0
	en = DEFAULT_FFT_SIZE
	fin= []
	num = 1
	sumdb = 0
	while (len(data) - en) > DEFAULT_FFT_SIZE:
		#frame size = DEFAULT_FFT_SIZE, one frame time: 0.37s
		sub_fin, sub_avgdb = fft_transformation(data=data[st:en],scale=scale) 
		fin.append(sub_fin)
		sumdb += sub_avgdb
		st = st + DEFAULT_FFT_SIZE/DEFAULT_OVERLAP
		en = st + DEFAULT_FFT_SIZE
		num += 1
 	return fin,sumdb/num

###########################################################
# save fingerprint and file information
# format {ID: filename}
###########################################################
def save_data(audio_name, tdata):
	yaml_data = open(data_path+"AudioFingerData.yml", 'r+')	
	catalog = yaml.load(yaml_data)
	if catalog != None:
		for key in catalog:
			if audio_name == key:
				yaml_data.close()
				print "saved before"
				return 0
	else:
		catalog ={}
	yaml_data = open(data_path+"AudioFingerData.yml", 'w+')
	catalog.update({audio_name:tdata})
	yaml.dump(catalog, yaml_data)

	yaml_data.close()
	print "save Audio-Fingerprint Done"

###########################################################
# audio_nanlysis
###########################################################
def audio_analysis(filename, data, framerate,channels, save,queue):
	
	broframe = detect_broken_frame(data, channels)

	tem_fin,avgdb = get_finger(data, framerate)
	finger = {}
	finger.update({str(channels):tem_fin})

	if save is True:
		queue.put(finger)
	else:
		audio_name, confidence = recognize(np.array(finger[str(channels)],dtype = np.uint32), channels, framerate)
		queue.put({channels:{"broken_frame":broframe,"name":audio_name,"confidence":confidence,"average_db":avgdb}})
		del finger

def auana(filename, save):
	#open wav file
	wf = wave.open(filename, 'rb')
	params = wf.getparams()
	nchannels, sampwidth, framerate, nframes = params[:4]
	str_data = wf.readframes(nframes)
	wf.close()

	wave_data = np.fromstring(str_data, dtype = np.short)
	wave_data.shape = -1,2
	wave_data = wave_data.T #transpose multiprocessing.Process

	filename = os.path.basename(filename)

	audio_analysis(filename, wave_data[0],framerate, 0, save, queue)
	audio_analysis(filename, wave_data[1],framerate, 1, save, queue)

	if save is True:
		fingerprint = {}
		fingerprint.update(queue.get())
		fingerprint.update(queue.get())
		save_data(filename, fingerprint)
	else:
		result = {}
		result.update(queue.get())
		result.update(queue.get())

		for i in  xrange(2):
			if result[i]["broken_frame"]!=0:
				print "+----------"
				print "| channel:%d, detect a broken frame, in time:"%i, result[i]["broken_frame"]
				print "+----------"
				return "Broken Frame",result[0]["average_db"]
		
		average_db = int((result[1]["average_db"]+result[1]["average_db"])/2)
		confidence = (result[0]["confidence"]+result[1]["confidence"])/2

		if (result[0]["name"]!=None) and (result[0]["name"] == result[1]["name"]):
			print "confidence:%3f"%confidence
			return result[0]["name"],average_db
		elif result[0]["name"] != result[1]["name"]:
			return "Channel Error",average_db
		else:
			return "Not Found",average_db

if __name__ == '__main__':
	dir_audio = 'C:\Users\b51762\Desktop\FITII\Auana-N/sample/'
	audio_list = []
	file_num = 0
	for parent, dirnames, filenames in os.walk(dir_audio):
		for filename in filenames:
			portion = os.path.splitext(filename)
			if portion[1] == '.wav':
				path = os.path.join(parent, filename)
				path = path.replace('\\','/')
				audio_list.append(path)
				file_num += 1
	print file_num
	auana("C:\Users/b51762\Desktop\FITII\Auana-N/audio_lib/source1.wav",True)
	auana("C:\Users/b51762\Desktop\FITII\Auana-N/audio_lib/source2.wav",True)
	print "........."
	
	# print auana("C:\Users/b51762\Desktop/127.wav",0)
	for a in  xrange(2,len(audio_list)):
		start = time.time()
		print audio_list[a]
		print auana(audio_list[a],0)
		print "Finished time: %4f"%(time.time()-start)
		print " "
	# import cProfile
	# cProfile.run("auana(dir_audio,0)")