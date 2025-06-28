package com.sy.imagesaver.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.sy.imagesaver.R

object AppIcons {
    val ImageType: ImageVector
        @Composable
        get() = ImageVector.vectorResource(id = R.drawable.ic_type_image)
    
    val VideoType: ImageVector
        @Composable
        get() = ImageVector.vectorResource(id = R.drawable.ic_type_video)
}

