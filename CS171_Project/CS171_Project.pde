//Name: Cormac Hynes
//Student No: 18450395
//CS171 End of Year Project

//Shooting range game 

import ddf.minim.*; //Minin library created by: Damien Di Fede and Anderson Mills
import ddf.minim.ugens.*;
import ddf.minim.analysis.*;
import ddf.minim.effects.*;
import ddf.minim.signals.*;
import ddf.minim.spi.*;

Minim minim;
AudioPlayer tune;
AudioOutput out,out2;
Sampler shot,dead,click;//audio samples 

boolean stop = false;
boolean press = false;
int game = 0;//variables
float x = 100;//x co ordinate
float y = 100;//y co ordinate
float x_dir=2;//x direction
float y_dir=0;//y direction
int score=0;//score variable
int life=5;//life count
int shoot=0;
int fire=0;
PImage bird1,sky1,grass1,gun1,lives,gun2,birdhit,birdkill,menu,mag,gun3,end;    //images   //TO DO LIST: Add bullet sprites & counter, More than 1 bird, Flip the bird when it falls, Reload animation, Make birds faster for higher scores,

void setup()
{
  size(700,450,P2D);
  surface.setResizable(false);
  frameRate(60);
  minim= new Minim(this);
  tune = minim.loadFile("tune.mp3");//Tune.mp3 is an audio loop that I created.
  tune.loop();
   out = minim.getLineOut();

   
  //game 1 sprites
  bird1 = loadImage("BirdFly.png");//load in sprites
  sky1 = loadImage("skyBG.png");
  grass1 = loadImage("Grass1.png");
  gun1 = loadImage("GunArms.png");
  birdkill = loadImage("BirdBoom.png");
  gun2 = loadImage("GunFire.png");
  birdhit = loadImage("BirdHit.png");
  menu = loadImage("MenuBG.png");  
  mag = loadImage("GunMag.png");
  gun3= loadImage("GunEmpty.png");
  end= loadImage("End.png");
  
  shot= new Sampler("handclap.wav",4,minim);
  dead= new Sampler("hit.wav",4,minim);
  click= new Sampler("CHH.wav",4,minim);//click sound for empty mag

  
  shot.patch(out);
  dead.patch(out);
  click.patch(out);

  
  
  
  
    
}
void draw()
{
  
  background(menu);
  
  //RE DO THIS FOR MENU WITH EXIT BUTTON
  
    if(game==1)//Game 1, standard game, shoot the penguins! //<>//
    {
     
       
       background(sky1); 
       float xbird = 65.2;
            
       if(x_dir<0)//These if statements are used to help orient the bird sprite correctly depending on direction
       {
         xbird=-65.2*1.5;
       }
       if(x_dir>0)
       {
         xbird=65.2*1.5;
       }
       if(y_dir!=0)
       {
       noStroke();
       pushMatrix();  //draw  blood splat
       beginShape();
       translate(x,y);
       texture(birdhit);
       vertex(-75,-50,0,0);
       vertex(75,-50,600,0);
       vertex(75,50,600,400);
       vertex(-75,50,0,400);
       endShape(CLOSE);
       popMatrix();
       }
       
       stroke(1);
       
       noStroke();
       pushMatrix();  //draw bird
       translate(x,y);
       if(y_dir!=0)//Rotate the bird as it falls
       {
         int r =1;
         if(x_dir<0)
         {
           r=-1;
         }
       rotate(90*r);
       }
       
       else
       {
         rotate(0);
       }
       beginShape();
       if(y_dir!=0)
       {
         texture(birdkill);//when the bird is hit, swap the texture
       }
       else
       {
       texture(bird1);
       }
       vertex(-xbird/2,-20*1.5,0,0);
       vertex(xbird/2,-20*1.5,947,0);
       vertex(xbird/2,20*1.5,947,581);
       vertex(-xbird/2,20*1.5,0,581);
       endShape(CLOSE);
       popMatrix();
       //end of bird
 
       pushMatrix();  //draw grass
       beginShape();
       translate(0,10);
       texture(grass1);
       vertex(0,0,0,0);
       vertex(600,0,600,0);
       vertex(600,400,600,400);
       vertex(0,400,0,400);
       endShape(CLOSE);
       popMatrix();
       //end of grass layer
       
       pushMatrix();  //draw gun
       beginShape();
       translate(mouseX,mouseY+10);
       if(mousePressed & fire>-900)//use a different sprite when the gun is fired
       {
         texture(gun2);//gun fired sprite
       }
       else if(mousePressed & fire<=-900)
       {
         texture(gun3);
       }
       else
       {
         texture(gun1);//gun in hand sprite
       }
       vertex(-450,-200,0,0);
       vertex(450,-200,600,0);
       vertex(450,400,600,400);
       vertex(-450,400,0,400);
       endShape(CLOSE);
       popMatrix();
       //end of first gun layer
       
       
       
       stroke(1);
       
       
       fill(0);
            
             
       x = x_dir+x;//x and y movement 
       y = y_dir+y;
      
       
       
       
       if(x>620 | x<-20)
       {
         int LR = (int)random(101);//Randomly decide to draw the bird flying L-->R, or R-->L
         if(LR<=50)
         {
           x=-10;
           y=(int)random(200);
           y_dir=0;
           if(score>50)
           {
             x_dir=random(4)+6;//Randomised velocity
           }
           else
           {
             x_dir=random(4)+2;
           }
           
         }
         else if(LR>50)
         {
           x=610;
           y=(int)random(200);
           y_dir=0;
            if(score>50)
           {
             x_dir=random(-4)-6;//Randomised velocity
           }
           else
           {
             x_dir=random(-4)-2;
           }
           
         }
         life--;

       }
              
        if(y>400)//when the bird falls off screen
       {
         int LR = (int)random(101);
         if(LR<=50)
         {
           x=-10;
           y=(int)random(200);
           y_dir=0;
            if(score>50)
           {
             x_dir=random(4)+6;//Randomised velocity
           }
           else
           {
             x_dir=random(4)+2;
           }
           
         }
         else if(LR>50)
         {
           x=610;
           y=(int)random(200);
           y_dir=0;
            if(score>50)
           {
             x_dir=random(-4)-6;//Randomised velocity
           }
           else
           {
             x_dir=random(-4)-2;
           }
           
         }
         if(score>50)
         {
           score=score+4;
         }
         else
         {
           score++;
         }
         dead.trigger();
         
         
       }
       
       
       if(mouseX>x-25 & mouseX<x+55 & mouseY>y-20 & mouseY<y+50 & shoot==1)//Hit Box. NOTE: There is a bug with this system, but other alternatives are buggier/ PLEASE BE HONEST, dont hold down the mouse button
       {
         shoot=0;
         
         if(x_dir>0)
         {
           x_dir=0.01;//minute pos/neg to keep the sprites oriented correctly, also stop x axis movement
         }
         if(x_dir<0)
         {
           x_dir=-0.01;
         }
         
         y_dir=6;
         y=y_dir+y; 
         
                
       }
       else
       {
         shoot=0;
       }
       
       
       
     
       noStroke();//Status bar
       fill(240,100,100);
       rect(0,450,600,-50);
       
       stroke(1);
       fill(0);
       textSize(40);
       text("Lives: "+life,0,440);//display lives and score
       text(("Score: "+score),400,440);
       
       //ammo bar
       noStroke();
       fill(100);
       rect(600,0,100,450);
       
       pushMatrix();  //draw  mag
       beginShape();
       translate(600,fire);
       texture(mag);
       vertex(0,0,0,0);
       vertex(100,0,200,0);
       vertex(100,1000,200,2000);
       vertex(0,1000,0,2000);
       endShape(CLOSE);
       popMatrix();
       
       
       
       if(life<=0)//GAME OVER when you run out of lives
       {
         delay(200);
         println("High Score: "+score);
         game=2;
         life=5;//reset lives and score for new game
         score=0;
         
       }
       
       if(fire<=-900)
       {
         fill(255,0,0);
         textSize(30);
         text("QUICK!!! keep pressing R to reload",50,300);
       }
       
       
       
       
    }//End of game 
    
    if(game==1)
    {
      strokeWeight(2);//reticule
      stroke(255,0,0);
      line(mouseX,mouseY-20,mouseX,mouseY+20);
      line(mouseX-20,mouseY,mouseX+20,mouseY);
      fill(0,0,0,100);
      ellipse(mouseX,mouseY,30,30);
      ellipse(mouseX,mouseY,15,15);
    }
      stroke(1);
      
      if(game==2)//game over screen
      {
        tune.close();
       
        background(sky1);
        noStroke();
        
       pushMatrix();  //draw grass
       beginShape();
       translate(0,10);
       texture(grass1);
       vertex(0,0,0,0);
       vertex(600,0,600,0);
       vertex(600,400,600,400);
       vertex(0,400,0,400);
       endShape(CLOSE);
       popMatrix();
       
       pushMatrix();  //draw bird attack
       beginShape();
       translate(350,225);
       texture(end);
       vertex(-2*frameCount,-2*frameCount,0,0);
       vertex(2*frameCount,-2*frameCount,500,0);
       vertex(2*frameCount,2*frameCount,500,500);
       vertex(-2*frameCount,2*frameCount,0,500);
       endShape(CLOSE);
       popMatrix();
       
       if(frameCount*4>1000)
       {
         fill(255,0,0,200);
         rect(0,0,700,450);
         fill(0);
         text("GAME OVER",10,50);
         text("Press ESCAPE to exit",10,150);
         
       }
      
      
        
      }
      
      
      if(game==3)//pause screen
      {
        background(100,150,100);
        noStroke();
        fill(100,100,100,220);
        rect(0,0,600,450);
        fill(0);
        text("Game Paused",50,50);
      }
      
}
      
    
    
void mouseClicked() {
  if(game==1 & fire>-900)
  {
    shot.trigger();
    fire=fire-100;
  }
  else if(game==1 & fire<=-900)
  {
    click.trigger();
  }
}
void mousePressed()
{
  if(fire>-900  & game==1)
  {
    shoot=1;
    frameCount=0;
  }
  if(frameCount>10)
  {
    shoot=0;
  }
}
void mouseReleased()
{
  shoot=0;
}


void keyPressed()
{
  if(key==' ')//space pauses and unpauses the game
  {
    if(game==1)
    {
      game=3;
    }
    else if(game==3)
    {
      game=1;
    }
  }
  if(key=='r')
  {
    if(second()%2==0)
    {    
      fire=0;
    }
  }
  
  if(keyCode == ENTER & game<=0)//ente to start the game
  {
    game=1;
  }
  if(keyCode == ESC)//escape to exit
  {
    exit();
  }
}
