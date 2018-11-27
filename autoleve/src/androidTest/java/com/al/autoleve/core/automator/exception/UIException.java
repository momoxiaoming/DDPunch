package com.al.autoleve.core.automator.exception;


import com.al.autoleve.core.automator.ext.Status;

public class UIException extends Exception
{
    private Status.StatueCode code;
    private String description;

    public UIException(Status.StatueCode code, String description)
    {
        this.code =code;
        this.description = description;
    }

    public Status.StatueCode getErrorCode()
    {
        return code;
    }

    public String getDescription()
    {
        return description;
    }

    @Override
    public String toString()
    {
        return "StatueCode:"+ code.value() + " description:"+description;
    }
}
