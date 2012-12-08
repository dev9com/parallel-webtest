package com.dynacrongroup.webtest.parameter;

import com.google.common.base.Joiner;

public class TestCombo extends ParameterCombination {

    private String param = "";

    private String anotherParam = "";

    public void setParam(String param) {
        this.param = param;
    }

    public String getParam() {
        return param;
    }

    public String getAnotherParam() {
        return anotherParam;
    }

    public void setAnotherParam(String anotherParam) {
        this.anotherParam = anotherParam;
    }

    public String toString() {
        return Joiner.on("|").join(super.toString(),param, anotherParam);
    }

}