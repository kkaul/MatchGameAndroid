package com.example.memory_game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Manager extends Activity 
{
    private static int ROW_COUNT = -1;
	private static int COL_COUNT = -1;
	private Context context;
	private Drawable backImage;
	private int [] [] cards;
	private List<Drawable> images;
	private Card firstCard;
	private Card seconedCard;
	private ButtonListener buttonListener;
	
	private static Object lock = new Object();
	
	int turns;
	private TableLayout mainTable;
	private UpdateCardsHandler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);  
        handler = new UpdateCardsHandler();
        loadImages();
        setContentView(R.layout.main);
       backImage =  getResources().getDrawable(R.drawable.icon);
      
       buttonListener = new ButtonListener();
        
        mainTable = (TableLayout)findViewById(R.id.TableLayout03);
        
        
        context  = mainTable.getContext();
        int x,y;
        x = 3; y = 5;
	  	newGame(x,y);
	  			
	  }

    
    private void newGame(int c, int r) 
    {
    	ROW_COUNT = r;
    	COL_COUNT = c;
    	
    	cards = new int [COL_COUNT] [ROW_COUNT];
    	
    	
    	mainTable.removeView(findViewById(R.id.TableRow01));
    	mainTable.removeView(findViewById(R.id.TableRow02));
    	
    	TableRow tr = ((TableRow)findViewById(R.id.TableRow03));
    	tr.removeAllViews();
    	
    	mainTable = new TableLayout(context);
    	tr.addView(mainTable);
    	
    	 for (int y = 0; y < ROW_COUNT; y++) 
    	 {
    		 mainTable.addView(createRow(y));
          }
    	 
    	 firstCard=null;
    	 loadCards();
    	 
    	 turns=0;
    	 ((TextView)findViewById(R.id.attempts)).setText("Tries: "+turns);
    	 
			
	}
    
    private void loadImages() 
    {
    	images = new ArrayList<Drawable>();
    	
    	images.add(getResources().getDrawable(R.drawable.card1));
    	images.add(getResources().getDrawable(R.drawable.card2));
    	images.add(getResources().getDrawable(R.drawable.card3)); 
    	images.add(getResources().getDrawable(R.drawable.card4)); 
    	images.add(getResources().getDrawable(R.drawable.card5)); 
    	images.add(getResources().getDrawable(R.drawable.card6)); 
    	images.add(getResources().getDrawable(R.drawable.card7)); 
    	images.add(getResources().getDrawable(R.drawable.card8)); 
    	images.add(getResources().getDrawable(R.drawable.card9)); 
    	images.add(getResources().getDrawable(R.drawable.card10)); 
    	images.add(getResources().getDrawable(R.drawable.card11)); 
    	images.add(getResources().getDrawable(R.drawable.card12)); 
    	
	}

	private void loadCards()
	{
		try
		{
	    	int size = ROW_COUNT*COL_COUNT;
	    	
	    	Log.i("loadCards()","size=" + size);
	    	
	    	ArrayList<Integer> list = new ArrayList<Integer>();
	    	
	    	for(int i=0;i<size;i++)
	    	{
	    		list.add(new Integer(i));
	    	}
	    	
	    	
	    	Random r = new Random();
	    
	    	for(int i=size-1;i>=0;i--)
	    	{
	    		int t=0;
	    		
	    		if(i>0)
	    		{
	    			t = r.nextInt(i);
	    		}
	    		
	    		t=list.remove(t).intValue();
	    		cards[i%COL_COUNT][i/COL_COUNT]=t%(size/2);
	    		
	    		Log.i("loadCards()", "card["+(i%COL_COUNT)+
	    				"]["+(i/COL_COUNT)+"]=" + cards[i%COL_COUNT][i/COL_COUNT]);
	    	}
	    }
		catch (Exception e) 
		{
			Log.e("loadCards()", e+"");
		}
		
    }
    
    private TableRow createRow(int y)
    {
    	 TableRow row = new TableRow(context);
    	 TableLayout.LayoutParams tableRowParams=
    			  new TableLayout.LayoutParams
    			  (TableLayout.LayoutParams.FILL_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);

    			int leftMargin=10;
    			int topMargin=0;
    			int rightMargin=10;
    			int bottomMargin=20;

    			tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

    			row.setLayoutParams(tableRowParams);

    	 row.setHorizontalGravity(Gravity.CENTER);
         
         for (int x = 0; x < COL_COUNT; x++) {
		         row.addView(createImageButton(x,y));

         }

         return row;
    }
    
    private View createImageButton(int x, int y)
    {
    	Button button = new Button(context);
    	button.setBackgroundDrawable(backImage);
    	button.setId(100*x+y);
    	button.setOnClickListener(buttonListener);
    	return button;
    }
    
    class ButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			
			synchronized (lock) {
				if(firstCard!=null && seconedCard != null){
					return;
				}
				int id = v.getId();
				int x = id/100;
				int y = id%100;
				turnCard((Button)v,x,y);
			}
			
		}
		
		private void turnCard(Button button,int x, int y) {
			button.setBackgroundDrawable(images.get(cards[x][y]));
			
			if(firstCard==null){
				firstCard = new Card(button,x,y);
			}
			else{ 
				
				if(firstCard.x == x && firstCard.y == y){
					return; 
				}
					
				seconedCard = new Card(button,x,y);
				
				turns++;
				((TextView)findViewById(R.id.attempts)).setText("Tries: "+turns);
				
			
				TimerTask tt = new TimerTask() {
					
					@Override
					public void run() {
						try{
							synchronized (lock) {
							  handler.sendEmptyMessage(0);
							}
						}
						catch (Exception e) {
							Log.e("E1", e.getMessage());
						}
					}
				};
				
				  Timer t = new Timer(false);
			        t.schedule(tt, 800);
			}
			
				
		   }
			
		}
    
    class UpdateCardsHandler extends Handler
    {
    	
    	@Override
    	public void handleMessage(Message msg) 
    	{
    		synchronized (lock) 
    		{
    			checkCards();
    		}
    	}
    	 @SuppressWarnings("deprecation")
		public void checkCards()
    	 {
    	    	if(cards[seconedCard.x][seconedCard.y] == cards[firstCard.x][firstCard.y])
    	    	{
    				firstCard.button.setVisibility(View.INVISIBLE);
    				seconedCard.button.setVisibility(View.INVISIBLE);
    			}
    			else 
    			{
    				seconedCard.button.setBackgroundDrawable(backImage);
    				firstCard.button.setBackgroundDrawable(backImage);
    			}
    	    	
    	    	firstCard=null;
    			seconedCard=null;
    	    }
    }
    
   
    
    
}
