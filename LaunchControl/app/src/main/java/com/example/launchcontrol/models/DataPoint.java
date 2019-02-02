package com.example.launchcontrol.models;

public class DataPoint {

    private String rawMessage;

    //VIN
    private String VIN;

    //Engine Parameters
    private float RPM;
    private float calculatedEngineLoad;
    private float engineCoolantTemperature;
    private float absoluteEngineLoad;
    private float engineOilTemperature;
    private float torquePercentage;
    private float referenceTorque;

    //Intake/Exhaust
    private float intakeTemperature;
    private float intakePressure;
    private float flowPressure;
    private float barometricPressure;

    //Speed/Time
    private float vehicleSpeed;
    private float engineRunningTime;
    private float vehicleRunningDistance;

    //Driver
    private float throttlePosition;
    private float ambientTemperature;

    //Electric Systems
    private float controlModuleVoltage;

    //Fuel
    private float fuelLevel;

    public DataPoint(String message)
    {
        processMessage(message);
    }

    //Process comma separated values
    private void processMessage(String message)
    {
        String[] parameters = message.split(",");

        rawMessage = message;

        //VIN
        try
        {
            VIN = parameters[0];
        } catch (Exception e) {}


        //Engine Parameters
        try
        {
            RPM = Float.parseFloat(parameters[1]);
        } catch (Exception e) {}
        try
        {
            calculatedEngineLoad =  Float.parseFloat(parameters[2]);
        } catch (Exception e) {}
        try
        {
            engineCoolantTemperature = Float.parseFloat(parameters[3]);
        } catch (Exception e) {}
        try
        {
            absoluteEngineLoad = Float.parseFloat(parameters[4]);
        } catch (Exception e) {}
        try
        {
            engineOilTemperature = Float.parseFloat(parameters[5]);
        } catch (Exception e) {}

        try
        {
            torquePercentage = Float.parseFloat(parameters[6]);
        } catch (Exception e) {}

        try
        {
            referenceTorque = Float.parseFloat(parameters[7]);
        } catch (Exception e) {}


        //Intake/Exhaust
        try
        {
            intakeTemperature = Float.parseFloat(parameters[8]);
        } catch (Exception e) {}

        try
        {
            intakePressure = Float.parseFloat(parameters[9]);
        } catch (Exception e) {}

        try
        {
            flowPressure = Float.parseFloat(parameters[10]);
        } catch (Exception e) {}

        try
        {
            barometricPressure = Float.parseFloat(parameters[11]);
        } catch (Exception e) {}

        //Speed/Time
        try
        {
            vehicleSpeed = Float.parseFloat(parameters[12]);
        } catch (Exception e) {}

        try
        {
            engineRunningTime = Float.parseFloat(parameters[13]);
        } catch (Exception e) {}

        try
        {
            vehicleRunningDistance = Float.parseFloat(parameters[14]);
        } catch (Exception e) {}

        //Driver
        try
        {
            throttlePosition = Float.parseFloat(parameters[15]);
        } catch (Exception e) {}

        try
        {
            ambientTemperature = Float.parseFloat(parameters[16]);
        } catch (Exception e) {}

        //Electric Systems
        try
        {
            controlModuleVoltage = Float.parseFloat(parameters[17]);
        } catch (Exception e) {}

        //Fuel
        try
        {
            fuelLevel = Float.parseFloat(parameters[18]);
        } catch (Exception e) {}
    }


    public String getVIN()
    {
        return (VIN != null) ? VIN : "";
    }

    public float getCalculatedEngineLoad()
    {
        return calculatedEngineLoad;
    }

    public float getEngineCoolantTemperature()
    {
        return engineCoolantTemperature;
    }

    public float getAbsoluteEngineLoad()
    {
        return absoluteEngineLoad;
    }

    public float getTorquePercentage()
    {
        return torquePercentage;
    }

    public float getReferenceTorque()
    {
        return referenceTorque;
    }

    public float getIntakeTemperature()
    {
        return intakeTemperature;
    }

    public float getFlowPressure()
    {
        return flowPressure;
    }

    public float getBarometricPressure()
    {
        return barometricPressure;
    }

    public float getVehicleSpeed()
    {
        return vehicleSpeed;
    }

    public float getEngineRunningTime()
    {
        return engineRunningTime;
    }

    public float getVehicleRunningDistance()
    {
        return vehicleRunningDistance;
    }

    public float getThrottlePosition()
    {
        return throttlePosition;
    }

    public float getAmbientTemperature()
    {
        return ambientTemperature;
    }

    public float getControlModuleVoltage()
    {
        return controlModuleVoltage;
    }

    public float getFuelLevel()
    {
        return fuelLevel;
    }

    @Override
    public String toString()
    {
        return rawMessage;
    }

}
