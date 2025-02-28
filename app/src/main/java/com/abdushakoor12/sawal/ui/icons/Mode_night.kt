package com.abdushakoor12.sawal.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Mode_night: ImageVector
    get() {
        if (_Mode_night != null) {
            return _Mode_night!!
        }
        _Mode_night = ImageVector.Builder(
            name = "Mode_night",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(380f, 800f)
                quadToRelative(133f, 0f, 226.5f, -93.5f)
                reflectiveQuadTo(700f, 480f)
                reflectiveQuadToRelative(-93.5f, -226.5f)
                reflectiveQuadTo(380f, 160f)
                horizontalLineToRelative(-21f)
                quadToRelative(-10f, 0f, -19f, 2f)
                quadToRelative(57f, 66f, 88.5f, 147.5f)
                reflectiveQuadTo(460f, 480f)
                reflectiveQuadToRelative(-31.5f, 170.5f)
                reflectiveQuadTo(340f, 798f)
                quadToRelative(9f, 2f, 19f, 2f)
                close()
                moveToRelative(0f, 80f)
                quadToRelative(-53f, 0f, -103.5f, -13.5f)
                reflectiveQuadTo(180f, 826f)
                quadToRelative(93f, -54f, 146.5f, -146f)
                reflectiveQuadTo(380f, 480f)
                reflectiveQuadToRelative(-53.5f, -200f)
                reflectiveQuadTo(180f, 134f)
                quadToRelative(46f, -27f, 96.5f, -40.5f)
                reflectiveQuadTo(380f, 80f)
                quadToRelative(83f, 0f, 156f, 31.5f)
                reflectiveQuadTo(663f, 197f)
                reflectiveQuadToRelative(85.5f, 127f)
                reflectiveQuadTo(780f, 480f)
                reflectiveQuadToRelative(-31.5f, 156f)
                reflectiveQuadTo(663f, 763f)
                reflectiveQuadToRelative(-127f, 85.5f)
                reflectiveQuadTo(380f, 880f)
                moveToRelative(80f, -400f)
            }
        }.build()
        return _Mode_night!!
    }

private var _Mode_night: ImageVector? = null
