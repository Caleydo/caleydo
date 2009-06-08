package org.caleydo.core.view.opengl.canvas.remote;

import java.util.TimerTask;

public class AnimationTimerTask
	extends TimerTask {

	int val = 0;
	@Override
	public void run() {
		
		if(val < 10)
		{
			val++;
			AnimatedGraphDrawing.setFirstViewValue(val);
		}
		else if ((val >= 10) && (val <20)){
			AnimatedGraphDrawing.setFirstViewValue(10);
			AnimatedGraphDrawing.setSecondViewValue(val-10);
			val++;
		}
		else if ((val >= 20) && (val <30)){
			AnimatedGraphDrawing.setFirstViewValue(10);
			AnimatedGraphDrawing.setSecondViewValue(10);
			AnimatedGraphDrawing.setThirdViewValue(val-20);
			val++;
		}
		else if ((val >= 30) && (val <40)){
			AnimatedGraphDrawing.setFirstViewValue(10);
			AnimatedGraphDrawing.setSecondViewValue(10);
			AnimatedGraphDrawing.setThirdViewValue(10);
			AnimatedGraphDrawing.setForthViewValue(val-30);
			val++;
		}
		else if ((val >= 40) && (val <50)){
			AnimatedGraphDrawing.setFirstViewValue(10);
			AnimatedGraphDrawing.setSecondViewValue(10);
			AnimatedGraphDrawing.setThirdViewValue(10);
			AnimatedGraphDrawing.setForthViewValue(10);
			AnimatedGraphDrawing.setFifthViewValue(val-40);
			val++;
		}
		else if (val >= 50){
			AnimatedGraphDrawing.setFirstViewValue(-1);
			AnimatedGraphDrawing.setSecondViewValue(-1);
			AnimatedGraphDrawing.setThirdViewValue(-1);
			AnimatedGraphDrawing.setForthViewValue(-1);
			AnimatedGraphDrawing.setFifthViewValue(-1);
			this.cancel();
		}

	}

}
