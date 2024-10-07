package com.todokanai.musicplayer.compose.dialog.pathpicker.holder

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.todokanai.filemanager.tools.independent.readableFileSize_td
import com.todokanai.musicplayer.compose.IconsRepository
import com.todokanai.musicplayer.myobjects.MyObjects.asyncImageExtension
import java.io.File
import java.text.DateFormat

@Composable
fun FileHolder(
    file: File,
    onClick:(File)->Unit,
    modifier: Modifier = Modifier,
){
    val sizeText =
        if (file.isDirectory) {
        val subFiles = file.listFiles()
        if (subFiles == null) {
            "null"
        } else {
            "${subFiles.size} 개"
        }
    } else {
        file.length().readableFileSize_td()
    }

    ConstraintLayout(
        modifier = modifier
            .height(60.dp)
            .fillMaxWidth()
            .clickable { onClick(file) }
    ) {
        val (fileImage, fileName, fileDate, fileSize) = createRefs()

        val imageModifier = Modifier
            .constrainAs(fileImage) {
                start.linkTo(parent.start)
            }
            .aspectRatio(1f, false)
            .width(50.dp)
            .fillMaxHeight()
            .padding(5.dp)
        if(asyncImageExtension.contains(file.extension)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(file.toUri())
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = imageModifier,
                placeholder = painterResource(id = IconsRepository().thumbnail(file))
            )
        } else{
            Image(
                modifier = imageModifier,
                painter = painterResource(id = IconsRepository().thumbnail(file)),
                contentDescription = null
            )
        }

        Text(
            text = sizeText,
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
            text = file.name,
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
            text = DateFormat.getDateTimeInstance().format(file.lastModified()),
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
}

@Preview
@Composable
private fun FileHolderPreview(){
    val testFile = File("TestPath")
    Surface {
        FileHolder(
            file = testFile,
            onClick = {},
            modifier = Modifier
        )
    }
}