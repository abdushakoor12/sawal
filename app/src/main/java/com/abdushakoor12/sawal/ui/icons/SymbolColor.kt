package com.abdushakoor12.sawal.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SymbolColor: ImageVector
    get() {
        if (_SymbolColor != null) {
            return _SymbolColor!!
        }
        _SymbolColor = ImageVector.Builder(
            name = "SymbolColor",
            defaultWidth = 16.dp,
            defaultHeight = 16.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.EvenOdd
            ) {
                moveTo(8f, 1.003f)
                arcToRelative(7f, 7f, 0f, isMoreThanHalf = false, isPositiveArc = false, -7f, 7f)
                verticalLineToRelative(0.43f)
                curveToRelative(0.09f, 1.51f, 1.91f, 1.79f, 3f, 0.7f)
                arcToRelative(
                    1.87f,
                    1.87f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    2.64f,
                    2.64f
                )
                curveToRelative(-1.1f, 1.16f, -0.79f, 3.07f, 0.8f, 3.2f)
                horizontalLineToRelative(0.6f)
                arcToRelative(7f, 7f, 0f, isMoreThanHalf = true, isPositiveArc = false, 0f, -14f)
                lineToRelative(-0.04f, 0.03f)
                close()
                moveToRelative(0f, 13f)
                horizontalLineToRelative(-0.52f)
                arcToRelative(
                    0.58f,
                    0.58f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    -0.36f,
                    -0.14f
                )
                arcToRelative(
                    0.56f,
                    0.56f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    -0.15f,
                    -0.3f
                )
                arcToRelative(
                    1.24f,
                    1.24f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    0.35f,
                    -1.08f
                )
                arcToRelative(
                    2.87f,
                    2.87f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    0f,
                    -4f
                )
                arcToRelative(
                    2.87f,
                    2.87f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    -4.06f,
                    0f
                )
                arcToRelative(
                    1f,
                    1f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    -0.9f,
                    0.34f
                )
                arcToRelative(
                    0.41f,
                    0.41f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    -0.22f,
                    -0.12f
                )
                arcToRelative(
                    0.42f,
                    0.42f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    -0.1f,
                    -0.29f
                )
                verticalLineToRelative(-0.37f)
                arcToRelative(6f, 6f, 0f, isMoreThanHalf = true, isPositiveArc = true, 6f, 6f)
                lineToRelative(-0.04f, -0.04f)
                close()
                moveTo(9f, 3.997f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = true, isPositiveArc = true, -2f, 0f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, 0f)
                close()
                moveToRelative(3f, 7.007f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = true, isPositiveArc = true, -2f, 0f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, 0f)
                close()
                moveToRelative(-7f, -5f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = true, isPositiveArc = false, 0f, -2f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0f, 2f)
                close()
                moveToRelative(7f, -1f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = true, isPositiveArc = true, -2f, 0f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, 0f)
                close()
                moveTo(13f, 8f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = true, isPositiveArc = true, -2f, 0f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, 0f)
                close()
            }
        }.build()
        return _SymbolColor!!
    }

private var _SymbolColor: ImageVector? = null
