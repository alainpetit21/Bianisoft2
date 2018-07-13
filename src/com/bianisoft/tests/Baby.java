package com.bianisoft.tests;

class World{
	private static Human[] arHuman;
	
	public static Human get(String p_stType, String p_stID){
		return arHuman[0];
	}
}

class Human extends Thread{
	public void dumpGC(){
		//[...]
	}
	public void eat(){
		//[...]
	}
	public void sleep(int p_nNbMilliSec){
		//[...]
	}
}

class Adult extends Human{
	public boolean isSleeping(){return true;}
	public Baby clone(Adult p_objDaddy){
		Baby objNew= new Baby();
		
		objNew.objDaddy= p_objDaddy;
		objNew.objMommy = this;
		
		int waitTime= (int)(9*30.5*24*60*60*1000);
		
		try{
			wait(waitTime);
		}catch(Exception e){
		}
		
		return objNew;
	}
}

public class Baby extends Human{
	Adult objDaddy, objMommy;

			
	public void run(){
		while(true){
			dumpGC();
			sleep(3*60*60*1000);
			
			if(objDaddy.isSleeping() && objMommy.isSleeping()){
				objDaddy.interrupt();
				objMommy.interrupt();
				eat();
			}
		}
	}
	
	public static void main(String[] args){
		Adult objDaddy= (Adult)World.get("Adult", "Alain Petit");
		Adult objMommy= (Adult)World.get("Adult", "Araceli Orozco Morales");

		Baby objBaby= objMommy.clone(objDaddy);
		objBaby.start();
	}
}