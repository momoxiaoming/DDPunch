package com.al.autoleve.core.automator.exception;


import com.al.autoleve.core.automator.ext.Status;

public class ExeLuaException extends UIException
{
    public ExeLuaException(Status.StatueCode code, String description)
    {
        super(code, description);
    }
}
