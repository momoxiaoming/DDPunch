package com.al.autoleve.core.automator.ext;


public class Status
{

    private String errCd;
    private String errMsg;

    public Status(String errCd, String errMsg)
    {
        this.errCd = errCd;
        this.errMsg = errMsg;
    }

    public String getErrCd() {
        return errCd;
    }

    public void setErrCd(String errCd) {
        this.errCd = errCd;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public enum StatueCode
    {
        ACTION_SUCCESS("0"),
        GEN_QRCD_ERROR("-6"),
        GET_PAY_ACCOUNT_INFO_FAIL("-1"),
        CHECK_USER_INFO_ERROR("-2"),
        RECEPINT_IL_LEGAL("-4"),
        CHECK_RECEPINT_ERROR("-3"),
        EXE_LUA_FAIL("-5"),
        GET_ACC_MONENY_ERROR("-7"),
        DB_TRANS_INFO_EMPTY_ERROR("-8"),
        DB_TRANS_INFO_LOCATION_TIME_ERROR("-9");

        public String mValue;
        StatueCode(String value)
        {
            mValue = value;
        }

        public String value()
        {
            return mValue;
        }
    }
}
