/*
 *  Copyright 2009-present, Stephen Colebourne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.joda.money;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StreamCorruptedException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A package scoped class used to manage serialization efficiently.
 * <p>
 * This class is mutable and intended for use by a single thread.
 */
final class Ser implements Externalizable {

    /** Type for BigMoney. */
    static final byte BIG_MONEY = 'B';
    /** Type for Money. */
    static final byte MONEY = 'M';
    /** Type for CurrencyUnit. */
    static final byte CURRENCY_UNIT = 'C';  // not in use yet

    /** The type. */
    private byte type;
    /** The data object. */
    private Object object;

    /**
     * Constructor for serialization.
     */
    public Ser() {
    }

    /**
     * Constructor for package.
     * 
     * @param type  the type
     * @param object  the object
     */
    Ser(byte type, Object object) {
        this.type = type;
        this.object = object;
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs the data.
     *
     * @serialData One byte type code, then data specific to the type.
     * @param out  the output stream
     * @throws IOException if an error occurs
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(type);
        switch (type) {
            case BIG_MONEY: {
                BigMoney obj = (BigMoney) object;
                writeBigMoney(out, obj);
                return;
            }
            case MONEY: {
                Money obj = (Money) object;
                writeBigMoney(out, obj.toBigMoney());
                return;
            }
            case CURRENCY_UNIT: {
                CurrencyUnit obj = (CurrencyUnit) object;
                writeCurrency(out, obj);
                return;
            }
        }
        throw new InvalidClassException("Joda-Money bug: Serialization broken");
    }

    private void writeBigMoney(ObjectOutput out, BigMoney obj) throws IOException {
        writeCurrency(out, obj.getCurrencyUnit());
        byte[] bytes = obj.getAmount().unscaledValue().toByteArray();
        out.writeInt(bytes.length);
        out.write(bytes);
        out.writeInt(obj.getScale());
    }

    private void writeCurrency(ObjectOutput out, CurrencyUnit obj) throws IOException {
        out.writeUTF(obj.getCode());
        out.writeShort(obj.getNumericCode());
        out.writeShort(obj.getDefaultFractionDigits());
    }

    /**
     * Outputs the data.
     *
     * @param in  the input stream
     * @throws IOException if an error occurs
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        type = in.readByte();
        switch (type) {
            case BIG_MONEY: {
                object = readBigMoney(in);
                return;
            }
            case MONEY: {
                object = new Money(readBigMoney(in));
                return;
            }
            case CURRENCY_UNIT: {
                object = readCurrency(in);
                return;
            }
        }
        throw new StreamCorruptedException("Serialization input has invalid type");
    }

    private BigMoney readBigMoney(ObjectInput in) throws IOException {
        CurrencyUnit currency = readCurrency(in);
        byte[] bytes = new byte[in.readInt()];
        in.readFully(bytes);
        BigDecimal bd = new BigDecimal(new BigInteger(bytes), in.readInt());
        BigMoney bigMoney = new BigMoney(currency, bd);
        return bigMoney;
    }

    private CurrencyUnit readCurrency(ObjectInput in) throws IOException {
        String code = in.readUTF();
        CurrencyUnit singletonCurrency = CurrencyUnit.of(code);
        if (singletonCurrency.getNumericCode() != in.readShort()) {
            throw new InvalidObjectException("Deserialization found a mismatch in the numeric code for currency " + code);
        }
        if (singletonCurrency.getDefaultFractionDigits() != in.readShort()) {
            throw new InvalidObjectException("Deserialization found a mismatch in the decimal places for currency " + code);
        }
        return singletonCurrency;
    }

    /**
     * Returns the object that will replace this one.
     * 
     * @return the read object, should never be null
     */
    private Object readResolve() {
        return object;
    }

}
