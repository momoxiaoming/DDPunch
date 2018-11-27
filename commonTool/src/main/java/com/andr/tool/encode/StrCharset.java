package com.andr.tool.encode;

public enum StrCharset
{
	UTF_8("utf-8"), GBK("gbk");

	private String charset;

	private StrCharset(String charset)
	{
		this.charset = charset;
	}

	public String value()
	{
		return charset;
	}
}