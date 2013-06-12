/**
 * (C) 2011 Digi-Net Technologies, Inc.
 * 4420 Northwest 36th Avenue
 * Gainesville, FL 32606 USA
 * All rights reserved.
 */
package com.convert.analytics;

import com.convert.rice.client.protocol.MapReduce;
import com.convert.rice.client.protocol.MapReduce.Builder;
import com.convert.rice.client.protocol.MapReduce.GroupBy;
import com.convert.rice.client.protocol.MapReduce.MapFunction;
import com.convert.rice.client.protocol.MapReduce.ReduceFunction;

/**
 * @author Ghais Issa <ghais.issa@convertglobal.com>
 * 
 */
public enum Aggregation {

    MINUTE,
    HOUR,
    DAY;

    public int getMillis() {
        switch (this) {
        case MINUTE:
            return 60000;
        case HOUR:
            return 3600000;
        case DAY:
            return 86400000;
        }
        throw new AssertionError("Unkown aggregation type");
    }

    public MapReduce getMR() {
        Builder builder = MapReduce.newBuilder();
        MapFunction.Builder mapFcuntion = MapFunction.newBuilder().setGroupBy(
                GroupBy.newBuilder().setStep(this.getMillis()));
        ReduceFunction redunceFunction = ReduceFunction.SUM;
        return builder.setMapFunction(mapFcuntion)
                .setReduceFunction(redunceFunction)
                .build();
    }
}
