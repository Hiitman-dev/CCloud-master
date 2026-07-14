package com.pira.ccloud.screens.favorites

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pira.ccloud.data.model.FavoriteGroup
import com.pira.ccloud.data.model.FavoriteItem
import com.pira.ccloud.ui.theme.GlassAlertDialog

@Composable
fun DeleteAllFavoritesDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    GlassAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete All Favorites") },
        text = { Text("Are you sure you want to delete all favorites? This action cannot be undone.") },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CreateGroupDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    existingGroups: List<FavoriteGroup>
) {
    var groupName by remember { mutableStateOf("") }
    val context = LocalContext.current

    GlassAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Playlist") },
        text = {
            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text("Playlist Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (groupName.isNotBlank()) {
                        val existingGroup = existingGroups.find {
                            it.name.equals(groupName, ignoreCase = true) && !it.isDefault
                        }
                        if (existingGroup == null) {
                            onConfirm(groupName)
                            onDismiss()
                        } else {
                            Toast.makeText(context, "A playlist with this name already exists", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled = groupName.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RenameGroupDialog(
    group: FavoriteGroup,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    existingGroups: List<FavoriteGroup>
) {
    var newGroupName by remember { mutableStateOf(group.name) }
    val context = LocalContext.current

    GlassAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename Playlist") },
        text = {
            OutlinedTextField(
                value = newGroupName,
                onValueChange = { newGroupName = it },
                label = { Text("New Playlist Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (newGroupName.isNotBlank()) {
                        val existingGroup = existingGroups.find {
                            it.id != group.id &&
                            it.name.equals(newGroupName, ignoreCase = true) &&
                            !it.isDefault
                        }
                        if (existingGroup == null) {
                            onConfirm(newGroupName)
                            onDismiss()
                        } else {
                            Toast.makeText(context, "A playlist with this name already exists", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled = newGroupName.isNotBlank()
            ) {
                Text("Rename")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DeleteGroupDialog(
    group: FavoriteGroup,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    GlassAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Playlist") },
        text = {
            Text("Are you sure you want to delete the playlist \"${group.name}\"? This action cannot be undone.")
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun MoveToGroupDialog(
    item: FavoriteItem,
    groups: List<FavoriteGroup>,
    onDismiss: () -> Unit,
    onConfirm: (List<String>) -> Unit
) {
    var selectedGroups by remember { mutableStateOf<List<String>>(emptyList()) }

    GlassAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Playlists") },
        text = {
            LazyColumn {
                items(groups.filter { !it.isDefault }) { group ->
                    val isChecked = selectedGroups.contains(group.id)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedGroups = if (isChecked) {
                                    selectedGroups.filter { it != group.id }
                                } else {
                                    selectedGroups + group.id
                                }
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                selectedGroups = if (checked) {
                                    selectedGroups + group.id
                                } else {
                                    selectedGroups.filter { it != group.id }
                                }
                            }
                        )
                        Text(
                            text = group.name,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(selectedGroups)
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RemoveFavoriteDialog(
    item: FavoriteItem,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    GlassAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Remove from Favorites") },
        text = { Text("Are you sure you want to remove \"${item.title}\" from your favorites?") },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text("Remove")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
