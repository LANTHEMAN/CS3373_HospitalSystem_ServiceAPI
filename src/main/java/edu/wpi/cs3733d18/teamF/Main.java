package edu.wpi.cs3733d18.teamF;

public class Main{
    public static void main(String[] args){
        ServiceRequest sr = new ServiceRequest();
        try{
            sr.run(0,0,1900,1000, null, null, null);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Failure to start service requests");
        }
    }
}

