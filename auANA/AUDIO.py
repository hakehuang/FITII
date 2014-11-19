# -*- coding: utf-8 -*-
import pyaudio
from pyaudio import PyAudio, paInt16
import numpy as np
from datetime import datetime
import wave, os, sys,string, logging, time, threading
from socket import *
import struct

#get FreeKV path
current_directory = os.path.dirname(os.path.abspath(__file__))
main_path = os.path.dirname(os.path.dirname(current_directory))
main_path = main_path.replace('\\','/')
config_path = main_path + '/config/'
lib_path = main_path + '/lib/'
sys.path.append(lib_path)
#dejavu_path = main_path + '/dejavu/'
#sys.path.append(dejavu_path)

#from dejavu import Dejavu
#import MySQLdb
#from dejavu.recognize import FileRecognizer
#from dejavu.recognize import MicrophoneRecognizer
from getconfig import Getconfig

config_file = config_path + "config.xml"
config = Getconfig(config_file)

def creat_audio_contents():
    platform = config.getValue('platform')
    IDE_list = ['gcc_arm','atl','iar','kds','uv4']
    save_audio_path = 'C:/' + '/audio/' + platform
    exists = os.path.exists(save_audio_path)
    if not exists:
        os.makedirs(save_audio_path)
        for IDE_name in IDE_list:
            for target in ['Debug','Release']:
                p = save_audio_path + '/' + IDE_name + '/' + target
                os.makedirs(p)

def Audio_play_record(file_No):
    task1 = threading.Thread(target = Audio_record, args = (15,file_No,))
    task2 = threading.Thread(target = Audio_play)
    task1.start()
    task2.start()
    task1.join()
    task2.join()

def Audio_record(seconds,file_No):
    CHUNK = 8192
    CHANNELS = 2
    SAMPLING_RATE = 44100
    FORMAT = pyaudio.paInt16
    NUM = int(SAMPLING_RATE/CHUNK * seconds)
    save_buffer = []

    #get audio path
    compiler = config.getValue('IDE')
    platform = config.getValue('platform')
    target = config.getValue('target')
    audio_path = 'C:/' + '/audio/' + platform + '/' + compiler + '/' + target + '/'
    
    # save wav file
    def save_wave_file(filename,data):
        wf_save = wave.open(audio_path + filename, 'wb')
        wf_save.setnchannels(CHANNELS)
        wf_save.setsampwidth(pa.get_sample_size(FORMAT))
        wf_save.setframerate(SAMPLING_RATE)
        wf_save.writeframes("".join(data))
        wf_save.close()

    #open audio stream
    pa = PyAudio()
    stream = pa.open(format = FORMAT, 
                    channels = CHANNELS, 
                    rate = SAMPLING_RATE, 
                    input = True, 
                    frames_per_buffer = CHUNK)

    print "-> START RECORD AUDIO"
    while NUM:
        string_audio_data = stream.read(CHUNK)
        save_buffer.append(string_audio_data)
        NUM -= 1

    #close stream
    stream.stop_stream()
    stream.close()
    # #'No' means: the audio file number
    # if (file_No >= 17) and (file_No <=22):
    #     file_No += 4
    # elif file_No > 22:
    #     file_No += 8
    # filename = '%s'% file_No + ".wav"

    # #save file
    # save_wave_file(filename, save_buffer)
    # logging.info(">> RECORD DONE")
    # save_buffer = []
    

def Audio_play():
#play audio
    CHUNK =8192
    filename = current_directory + '/audio_lib/source1.wav'
    wf = wave.open(filename, 'rb')
    pa = PyAudio()
    stream =pa.open(format = pa.get_format_from_width(wf.getsampwidth()), channels = wf.getnchannels(), rate = wf.getframerate(), output = True)

    NUM = int(wf.getframerate()/CHUNK * 15)
    logging.info("-> START PLAY AUDIO")
    while NUM:
        data = wf.readframes(CHUNK)
        if data == " ": break
        stream.write(data)
        NUM -= 1
    stream.stop_stream()
    stream.close()
    logging.info(">> PLAY DONE")




def audio_send(file_path):
    target = ('10.193.102.57',1234)
    def pacage_head(file_path):
        filename = os.path.basename(file_path) 
        head = struct.pack('256sI',filename,os.stat(file_path).st_size)
        return head

    def send_file (name):
        #connect to sever
        ex = 0    
        s = socket(AF_INET, SOCK_STREAM)
        s.connect(target)
        print 'connect sever...'

        header = pacage_head(name)
        s.sendall (header)

        f= open(name, 'rb')
        n = 0
        while 1:
            data = f.read(1024)
            if not data: break
            s.sendall (data)
            n += 1
        s.close()
        f.close()
        print 'SEND DONE', n

    send_file (file_path)