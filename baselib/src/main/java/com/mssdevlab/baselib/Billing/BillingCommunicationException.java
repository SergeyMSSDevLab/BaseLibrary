package com.mssdevlab.baselib.Billing;

class BillingCommunicationException extends Exception
{

    BillingCommunicationException(Throwable cause)
    {
        super(cause);
    }

    BillingCommunicationException(String message)
    {
        super(message);
    }
}
