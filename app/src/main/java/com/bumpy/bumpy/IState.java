package com.bumpy.bumpy;

import android.content.res.Resources;
import android.widget.LinearLayout;

/**
 * Created by ronst on 12/29/2017.
 */

public interface IState {
    void InitActivity(DynamicActivity dynamicActivity, LinearLayout lm, Resources resource);
}
