package com.sy.imagesaver.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.painter.Painter
import com.sy.imagesaver.R

object AppIcons {
    val ImageType: ImageVector
        @Composable
        get() = ImageVector.vectorResource(id = R.drawable.ic_type_image)
    
    val VideoType: ImageVector
        @Composable
        get() = ImageVector.vectorResource(id = R.drawable.ic_type_video)

    val Bookmark: ImageVector
        @Composable
        get() = ImageVector.vectorResource(id = R.drawable.ic_bookmark)
        
    val CheckCircle: Painter
        @Composable
        get() = painterResource(id = R.drawable.ic_check_circle)

    val BookmarkFilled: Painter
        @Composable
        get() = painterResource(id = R.drawable.ic_bookmark_filled)

    val Filter: Painter
        @Composable
        get() = painterResource(id = R.drawable.ic_filter)

    val Trash: Painter
        @Composable
        get() = painterResource(id = R.drawable.ic_trash)

    val History: ImageVector
        @Composable
        get() = ImageVector.vectorResource(id = R.drawable.ic_history)
}

