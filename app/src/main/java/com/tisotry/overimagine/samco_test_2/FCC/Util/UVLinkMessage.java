package com.tisotry.overimagine.samco_test_2.FCC.Util;

/**
 * Created by user on 2017-11-29.
 */
public abstract class UVLinkMessage {
    protected byte uMessageId;

    public byte getuMessageId() {
        return uMessageId;
    }

    public abstract byte[] SerializeBody();

    public abstract void DeserializeBody(byte[] payload);
}
