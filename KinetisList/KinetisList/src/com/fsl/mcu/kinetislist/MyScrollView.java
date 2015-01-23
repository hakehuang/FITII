package com.fsl.mcu.kinetislist;

import android.content.Context;
import android.graphics.Rect;
import android.os.Message;
import android.os.Messenger;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {
	private static final String TAG = "MyScrollView class";
	
	public Rect visibleRect = new Rect();
	
	private Messenger theMessenger;
	private Thread thread;
	private drawWindow dlyMsg;
	private class drawWindow implements Runnable {
		private int newTop, newBottom;
		private long newTime;
		private boolean newMsg = false;
		@Override
		public void run() {
			try {
				while (true) {
					if (newMsg && ((System.currentTimeMillis() - newTime) > 30)) {
						theMessenger.send(Message.obtain(null, DBdefine.MSG.MSG_FILL_TABLE_LINE, newTop, newBottom));
						newMsg = false;
					}
					else
						Thread.sleep(10);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void visibleRange(int top, int bottom) {
			newTop = top;
			newBottom = bottom;
			newMsg = true;
			newTime = System.currentTimeMillis();
		}
	}

	public MyScrollView(Context context) {
		super(context);
		dlyMsg = new drawWindow();
		thread = new Thread(dlyMsg);
		thread.start();
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		this.getLocalVisibleRect(visibleRect);
		dlyMsg.visibleRange(visibleRect.top, visibleRect.bottom);
		super.onScrollChanged(l, t, oldl, oldt);
	}

	public void setMessenger(Messenger mmessenger) {
		theMessenger = mmessenger;
	}
}
