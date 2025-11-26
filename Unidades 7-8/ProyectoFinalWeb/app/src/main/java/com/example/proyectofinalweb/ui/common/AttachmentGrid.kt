package com.example.proyectofinalweb.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.proyectofinalweb.R
import com.example.proyectofinalweb.model.Attachment
import com.example.proyectofinalweb.model.MediaType

@Composable
fun AttachmentGrid(
    attachments: List<Attachment>,
    onAttachmentClick: (Attachment) -> Unit,
    onAttachmentDescriptionChange: (Attachment, String) -> Unit,
    isEditing: Boolean = false
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(attachments) { attachment ->
            AttachmentItem(
                attachment = attachment,
                onAttachmentClick = onAttachmentClick,
                onAttachmentDescriptionChange = onAttachmentDescriptionChange,
                isEditing = isEditing
            )
        }
    }
}

@Composable
fun AttachmentItem(
    attachment: Attachment,
    onAttachmentClick: (Attachment) -> Unit,
    onAttachmentDescriptionChange: (Attachment, String) -> Unit,
    isEditing: Boolean
) {
    Column(
        modifier = Modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .clickable(enabled = !isEditing) { onAttachmentClick(attachment) },
            contentAlignment = Alignment.Center
        ) {
            when (attachment.type) {
                MediaType.IMAGE, MediaType.VIDEO -> {
                    Image(
                        painter = rememberAsyncImagePainter(model = attachment.uri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    if (attachment.type == MediaType.VIDEO) {
                        Icon(
                            imageVector = Icons.Default.PlayCircleFilled,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                MediaType.AUDIO -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                MediaType.FILE -> {
                    // TODO: Handle file preview
                }
            }

            if (isEditing) {
                IconButton(
                    onClick = { onAttachmentClick(attachment) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove attachment",
                        tint = Color.White
                    )
                }
            }
        }

        if (isEditing) {
            OutlinedTextField(
                value = attachment.description,
                onValueChange = { onAttachmentDescriptionChange(attachment, it) },
                label = { Text(stringResource(R.string.description_label)) },
                modifier = Modifier.fillMaxWidth()
            )
        } else if (attachment.description.isNotBlank()) {
            Text(
                text = attachment.description,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
