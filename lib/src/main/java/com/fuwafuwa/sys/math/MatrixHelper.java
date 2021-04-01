package com.fuwafuwa.sys.math;

import android.graphics.Matrix;

import androidx.annotation.NonNull;

public class MatrixHelper {


    public boolean isInversable(@NonNull Matrix matrix) {
        Matrix matrix1 = new Matrix();
        return matrix.invert(matrix1);
    }

    /**
     * 求逆矩阵
     * @param matrix
     * @return
     */
    public static Matrix inverse(@NonNull Matrix matrix) {
        Matrix matrix1 = new Matrix();
        if (matrix.invert(matrix1)) {
            return matrix1;
        }
        return null;
    }

}
