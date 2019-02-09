/**
 * Creation Date: Friday, February 1st 2019
 * Original Author: Rohan Rao
 * Modifications by: Akash Patel
 * Contents of File: Model class that encapsulates a raw OBD-II string to a java object
 */
package com.example.launchcontrol.models;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataPoint {

    private String rawMessage;

    //VIN
    private String VIN;

    //Engine Parameters
    private int RPM;
    private int calculatedEngineLoad;
    private int engineCoolantTemperature;
    private int absoluteEngineLoad;
    private int engineOilTemperature;
    private int torquePercentage;
    private int referenceTorque;

    //Intake/Exhaust
    private int intakeTemperature;
    private int intakePressure;
    private int flowPressure;
    private int barometricPressure;

    //Speed/Time
    private int vehicleSpeed;
    private int engineRunningTime;
    private int vehicleRunningDistance;

    //Driver
    private int throttlePosition;
    private int ambientTemperature;

    //Electric Systems
    private int controlModuleVoltage;

    //Fuel
    private int fuelLevel;

    private String dateTimeStamp;

    private LatLng latLng;

    public DataPoint(String message)
    {
        processMessage(message);
    }

    //Process comma separated values
    private void processMessage(String message)
    {
        String[] parameters = message.split(",");

        rawMessage = message;

        //Set timestamp
        dateTimeStamp = DataPoint.getCurrentDateTimeStamp();

        //Set location

        //VIN
        try
        {
            VIN = parameters[0];
        } catch (Exception e) {}


        //Engine Parameters
        try
        {
            RPM = Integer.parseInt(parameters[1]);
        } catch (Exception e) {}
        try
        {
            calculatedEngineLoad =  Integer.parseInt(parameters[2]);
        } catch (Exception e) {}
        try
        {
            engineCoolantTemperature = Integer.parseInt(parameters[3]);
        } catch (Exception e) {}
        try
        {
            absoluteEngineLoad = Integer.parseInt(parameters[4]);
        } catch (Exception e) {}
        try
        {
            engineOilTemperature = Integer.parseInt(parameters[5]);
        } catch (Exception e) {}

        try
        {
            torquePercentage = Integer.parseInt(parameters[6]);
        } catch (Exception e) {}

        try
        {
            referenceTorque = Integer.parseInt(parameters[7]);
        } catch (Exception e) {}


        //Intake/Exhaust
        try
        {
            intakeTemperature = Integer.parseInt(parameters[8]);
        } catch (Exception e) {}

        try
        {
            intakePressure = Integer.parseInt(parameters[9]);
        } catch (Exception e) {}

        try
        {
            flowPressure = Integer.parseInt(parameters[10]);
        } catch (Exception e) {}

        try
        {
            barometricPressure = Integer.parseInt(parameters[11]);
        } catch (Exception e) {}

        //Speed/Time
        try
        {
            vehicleSpeed = Integer.parseInt(parameters[12]);
        } catch (Exception e) {}

        try
        {
            engineRunningTime = Integer.parseInt(parameters[13]);
        } catch (Exception e) {}

        try
        {
            vehicleRunningDistance = Integer.parseInt(parameters[14]);
        } catch (Exception e) {}

        //Driver
        try
        {
            throttlePosition = Integer.parseInt(parameters[15]);
        } catch (Exception e) {}

        try
        {
            ambientTemperature = Integer.parseInt(parameters[16]);
        } catch (Exception e) {}

        //Electric Systems
        try
        {
            controlModuleVoltage = Integer.parseInt(parameters[17]);
        } catch (Exception e) {}

        //Fuel
        try
        {
            fuelLevel = Integer.parseInt(parameters[18]);
        } catch (Exception e) {}
    }

    public String getVIN()
    {
        return (VIN != null) ? VIN : "";
    }

    public Integer getCalculatedEngineLoad()
    {
        return calculatedEngineLoad;
    }

    public Integer getEngineRPM()
    {
        return RPM;
    }

    public Integer getEngineCoolantTemperature()
    {
        return engineCoolantTemperature;
    }

    public Integer getEngineOilTemperature() { return engineOilTemperature; }

    public Integer getAbsoluteEngineLoad()
    {
        return absoluteEngineLoad;
    }

    public Integer getTorquePercentage()
    {
        return torquePercentage;
    }

    public Integer getReferenceTorque()
    {
        return referenceTorque;
    }

    public Integer getIntakeTemperature()
    {
        return intakeTemperature;
    }

    public Integer getFlowPressure()
    {
        return flowPressure;
    }

    public Integer getBarometricPressure()
    {
        return barometricPressure;
    }

    public Integer getVehicleSpeed()
    {
        return vehicleSpeed;
    }

    public Integer getEngineRunningTime()
    {
        return engineRunningTime;
    }

    public Integer getVehicleRunningDistance()
    {
        return vehicleRunningDistance;
    }

    public Integer getThrottlePosition()
    {
        return throttlePosition;
    }

    public Integer getAmbientTemperature()
    {
        return ambientTemperature;
    }

    public Integer getControlModuleVoltage()
    {
        return controlModuleVoltage;
    }

    public Integer getFuelLevel()
    {
        return fuelLevel;
    }

    private static String getCurrentDateTimeStamp()
    {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        return sdfDate.format(new Date());
    }

    public void setLocation(Location location)
    {
        if (latLng == null && location != null)
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }


    @Override
    public String toString()
    {
        Gson gson = new Gson();
        String jsonObjectString = gson.toJson(this);
        return jsonObjectString;
    }






}
