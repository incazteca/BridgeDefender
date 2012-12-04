package com.franco.bridge;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.SurfaceHolder;

class BridgeDefenderThread extends Thread{
	
	/*
     * Physics constants
     */
    public static final int PHYS_DOWN_ACCEL_SEC = 35;
    public static final int PHYS_FIRE_ACCEL_SEC = 80;
    public static final int PHYS_SPEED_INIT = 30;
    public static final int PHYS_SPEED_MAX = 120;
    
    /*
     * State-tracking constants
     */
    public static final int STATE_RUNNING = 1;

    
    /*
     * Bitmaps and drawables that are used for the game
     */
    
    private Bitmap backGroundImage;
    private Bitmap bridge;

    
    private Drawable bomb;
    private Drawable player;
    private Drawable []explosionSequence;
    private Drawable pellet;
    
    /*
     * Size of sprites in pixels
     */
    
    //We'll use the bomb height and width for the explosion sequence sprites
    private int bombHeight;
    private int bombWidth;
    
    private int playerHeight;
    private int playerWidth;
    
    private int pelletHeight;
    private int pelletWidth;
    
    /*
     * Coords for player and pellet
     */
    
    private int playerX;
    private int playerY;
    
    private int pelletX;
    private int pelletY;
    
    /*
     * actions
     */
    
    private boolean pelletFiring;
    private boolean playerMoving;
    
    //players move horizontally
    private double moveplayerX;
    
    //bombs move down pellets move up
    
    private double movepelletY;
    private double movebombY;
    
    //game state
    
    private int mMode;
    
    //states whether the surface has been created and is ready to draw
    private boolean mRun = false;
    
    /*
     * Size of Canvas
     */
    
    private int canvasHeight = 1;
    private int canvasWidth = 1;
    
    /*
     * Rest of the stuff
     */
    
    private Handler handler;
    private SurfaceHolder surfaceHolder;
    
    //used to figure out how much time has passed between frames
    private long mLastTime; 
    
    public BridgeDefenderThread(SurfaceHolder surfaceHolder, Context context, Handler handler){
    	this.surfaceHolder = surfaceHolder;
    	this.handler = handler;
    	mContext = context;
    	
    	Resources res = context.getResources();
    	
    	//get the drawable sprites
    	bomb = context.getResources().getDrawable(R.drawable.bomb);
    	player = context.getResources().getDrawable(R.drawable.bigman);
    	pellet = context.getResources().getDrawable(R.drawable.pellet);
    	
    	//create the array used for an explosion
    	explosionSequence[0] = context.getResources().getDrawable(R.drawable.explosion1);
    	explosionSequence[1] = context.getResources().getDrawable(R.drawable.explosion2);
    	explosionSequence[2] = context.getResources().getDrawable(R.drawable.explosion3);
    	explosionSequence[3] = context.getResources().getDrawable(R.drawable.explosion4);
    	explosionSequence[4] = context.getResources().getDrawable(R.drawable.smoke1);
    	explosionSequence[5] = context.getResources().getDrawable(R.drawable.smoke2);
    	explosionSequence[6] = context.getResources().getDrawable(R.drawable.smoke3);
    	explosionSequence[7] = context.getResources().getDrawable(R.drawable.smoke4);
    	
    	//load background as a bitmap 
    	
    	backGroundImage = BitmapFactory.decodeResource(res, R.drawable.background);
    	bridge = BitmapFactory.decodeResource(getResources(),R.drawable.bridgetile);
    	
    	//get sizes of sprites
    	bombHeight = bomb.getIntrinsicHeight();
    	bombWidth = bomb.getIntrinsicWidth();
    	
    	playerHeight = player.getIntrinsicHeight();
    	playerWidth = player.getIntrinsicWidth();
    	
    	pelletHeight = pellet.getIntrinsicHeight();
    	pelletWidth = pellet.getIntrinsicWidth();
    	
    	moveplayerX = 0;
    	movepelletY = 0;
    	movebombY = 0;
    	
    	pelletFiring = false;
    	playerMoving = false;
    	
    	
    	
    }
    
    /**
     * Start the game
     */
    
    public void doStart(){
    	synchronized (surfaceHolder){
    		int speedInit = PHYS_SPEED_INIT;
    		
    		pelletFiring = false;
    		
    		//start coordinates for player and first bomb
    		
    		//player starts off at bottom center
    		playerX = canvasWidth;
    		playerY = canvasHeight - (bombHeight*3);
    		
    		mLastTime = System.currentTimeMillis()+100;
    		
    		setState(STATE_RUNNING);
    	}
    }
    
    @Override
    public void run(){
    	while (mRun){
    		Canvas c = null;
    		try{
    			c = surfaceHolder.lockCanvas(null);
    			synchronized(surfaceHolder){
    				if (mMode == STATE_RUNNING) updatePhysics();
    				doDraw(c);
    			}
    		}
    		finally{
    			if (c!=null){
    				surfaceHolder.unlockCanvasAndPost(c);
    			}
    		}
    	}
    }
    
    public void setRunning(boolean b){
    	mRun = b;
    }
    
    public void setFiring(boolean firing){
    	synchronized (surfaceHolder){
    		pelletFiring = firing;
    	}
    }
    
    public void setState(int mode){
    	synchronized (surfaceHolder){
    		mMode = mode;
    	}
    }
    
    /* Callback invoked when the surface dimensions change. */
    public void setSurfaceSize(int width, int height) {
        // synchronized to make sure these all change atomically
        synchronized (surfaceHolder) {
            canvasWidth = width;
            canvasHeight = height;

            // don't forget to resize the background image
            backGroundImage = Bitmap.createScaledBitmap(
                    backGroundImage, width, height, true);
        }
    }
    
    boolean doKeyDown(int keyCode, KeyEvent msg){
    	boolean handled = false;
    	
    	synchronized (surfaceHolder){
    		if (mMode == STATE_RUNNING){
    			if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
    				//move player 3 pixels to the right
    				moveplayerX = 3;
    				handled = true;
    			}
    			else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
    				moveplayerX = -3;
    				handled = true;
    				
    			}
    			else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
    				setFiring(true);
    				handled = true;
    			}
    		}	
    	}
    	return handled;

    }
    
    boolean doKeyup(int keyCode, KeyEvent msg){
    	boolean handled = false;
    	
    	synchronized (surfaceHolder){	
    		if (mMode == STATE_RUNNING) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    setFiring(false);
                    handled = true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                        || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    moveplayerX = 0;
                    handled = true;
                }
            }	
    	}
    	return handled;
    }
    
    
    private void doDraw(Canvas canvas){
    	//draw out the background
    	canvas.drawBitmap(backGroundImage,0,0, null);
    	
    	//draw base of bridge
    	int height = bridge.getHeight();
    	int width = bridge.getWidth();
    	
    	//draw base of bridge
    	for (int y = canvasHeight-height; y > canvasHeight-(height*2);y = y-height){
    		for (int x =0; x<canvasWidth; x = x+ width){
    			canvas.drawBitmap(bridge,x,y,null);
    		}
    	}
    	
    	
    	//draw the player with their current location
    	canvas.save();
    	
    	if (pelletFiring){
    		
    	}
    	
    	canvas.restore();
    }
    
    private void updatePhysics(){
    	long now = System.currentTimeMillis();
    	
    	//do nothing if mLastTime is in future
    	if (mLastTime < now){
    		if (playerMoving){
    			
    		}
    		if (pelletFiring){
    			
    		}
    	}
    	
    	return;
    }
    
}

