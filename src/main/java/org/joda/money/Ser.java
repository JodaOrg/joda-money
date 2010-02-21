/*
 *  Copyright 2009-2010 Stephen Colebourne
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
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StreamCorruptedException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A package scoped class used to manage serialization efficiently.
 */
final class Ser implements Externalizable {

    /** Type for BigMoney. */
    static final byte BIG_MONEY = 'B';
    /** Type for Money. */
    static final byte MONEY = 'M';
    /** Type for CurrencyUnit. */
    static final byte CURRENCY_UNIT = 'C';  // not in use yet

    /** The type. */
    private byte iType;
    /** The data object. */
    private Object iObject;

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
        iType = type;
        iObject = object;
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs the data.
     *
     * @serialData One byte type code, then data specific to the type.
     * @param out  the output stream
     * @throws IOException if an error occurs
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(iType);
        switch (iType) {
            case BIG_MONEY: {
                BigMoney obj = (BigMoney) iObject;
                out.writeUTF(obj.getCurrencyUnit().getCurrencyCode());
                byte[] bytes = obj.getAmount().unscaledValue().toByteArray();
                out.writeInt(bytes.length);
                out.write(bytes);
                out.writeInt(obj.getScale());
                return;
            }
            case MONEY: {
                Money obj = (Money) iObject;
                out.writeObject(obj.toBigMoney());
                return;
            }
            case CURRENCY_UNIT: {
                CurrencyUnit obj = (CurrencyUnit) iObject;
                out.writeUTF(obj.getCurrencyCode());
                return;
            }
        }
        throw new InvalidClassException("Joda-Money bug: Serialization broken");
    }

    /**
     * Outputs the data.
     *
     * @param in  the input stream
     * @throws IOException if an error occurs
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        iType = in.readByte();
        switch (iType) {
            case BIG_MONEY: {
                CurrencyUnit currency = CurrencyUnit.of(in.readUTF());
                byte[] bytes = new byte[in.readInt()];
                in.readFully(bytes);
                BigDecimal bd = new BigDecimal(new BigInteger(bytes), in.readInt());
                iObject = new BigMoney(currency, bd);
                return;
            }
            case MONEY: {
                iObject = new Money((BigMoney) in.readObject());
                return;
            }
            case CURRENCY_UNIT: {
                iObject = CurrencyUnit.of(in.readUTF());
                return;
            }
        }
        throw new StreamCorruptedException("Serialization input has invalid type");
    }

    /**
     * Returns the object that will replace this one.
     * 
     * @return the read object, should never be null
     */
    private Object readResolve() {
        return iObject;
    }

}
