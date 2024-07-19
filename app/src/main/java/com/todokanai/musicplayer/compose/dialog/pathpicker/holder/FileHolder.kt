package com.todokanai.musicplayer.compose.dialog.pathpicker.holder

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.todokanai.musicplayer.compose.presets.image.ImageHolder
import com.todokanai.musicplayer.data.dataclass.FileHolderItem

@Composable
fun FileHolder(
    modifier: Modifier = Modifier,
    fileHolderItem : FileHolderItem
){
    ConstraintLayout(
        modifier = modifier

            .height(60.dp)
            .fillMaxWidth()
    ) {
        val (fileImage, fileName, fileDate, fileSize) = createRefs()
        ImageHolder(
            modifier = Modifier
                .constrainAs(fileImage) {
                    start.linkTo(parent.start)
                }
                .aspectRatio(1f, false)
                .width(50.dp)
                .fillMaxHeight()
                .padding(5.dp),
            isAsyncImage = (fileHolderItem.isAsyncImage),
            data = fileHolderItem.uri,
            icon = painterResource(id =  fileHolderItem.thumbnail)
        )

        Text(
            text = fileHolderItem.size,
            fontSize = 15.sp,
            maxLines = 1,
            modifier = Modifier
                .constrainAs(fileSize) {
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .wrapContentWidth()
                .padding(4.dp)
        )

        Text(
            text = fileHolderItem.name,
            fontSize = 18.sp,
            maxLines = 1,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .constrainAs(fileName) {
                    start.linkTo(fileImage.end)
                    end.linkTo(fileSize.start)
                    top.linkTo(parent.top)
                    width = Dimension.fillToConstraints
                }
                .height(30.dp)
                .padding(4.dp)
        )

        Text(
            text = fileHolderItem.lastModified,
            fontSize = 15.sp,
            maxLines = 1,
            modifier = Modifier
                .constrainAs(fileDate) {
                    start.linkTo(fileImage.end)
                    end.linkTo(fileSize.start)
                    bottom.linkTo(parent.bottom)
                    top.linkTo(fileName.bottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .padding(4.dp)
        )
    }
    println("Recomposition: FileHolder")
}

/*
@Preview
@Composable
private fun FileHolderPreview(){
    Surface {
        FileHolder(
           // modifier = Modifier,
            fileHolderItem = FileHolderItem(File("test"), "name", "size", "lastModified", R.drawable.ic_launcher_foreground),
        )
    }
}

 */