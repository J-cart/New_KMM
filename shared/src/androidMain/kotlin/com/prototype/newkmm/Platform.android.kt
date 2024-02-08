package com.prototype.newkmm

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.media.MediaRecorder
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.prototype.newkmm.domain.JournalEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class AndroidPlatform : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()
actual fun getViewPlatform(): Platform = AndroidPlatform()


actual class PlatformUtil(private val context: Context) {
    actual fun createAudioUtil(): MainAudioUtil = AudioUtil(context)

}


@Composable
fun registerPermission(
    context: Context,
    onInitPerms: (ManagedActivityResultLauncher<String, Boolean>) -> Unit
) {
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    onInitPerms(permissionLauncher)
}


actual class AudioUtilImpl(private val context: Context) {
    actual fun initUtil(): MainAudioUtil = AudioUtil(context)
}

actual interface MainAudioUtil {

    @Composable
    actual fun registerPermission(isRecording: (Boolean) -> Unit)


    actual fun startAudioProcess(scope: CoroutineScope, isRecording: (Boolean) -> Unit)
    actual fun stopAudioProcess(scope: CoroutineScope, isRecording: (Boolean) -> Unit)
}


class AudioUtil(private val context: Context) : MainAudioUtil {
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var file: File
    // private var isRecording = false

    private var timerJob: Job? = null
    private var msToFinishOnResume: Long = 0
    private lateinit var coroutineScope: CoroutineScope

    private lateinit var exoPlayer: ExoPlayer

    private lateinit var permissionLauncher: ManagedActivityResultLauncher<String, Boolean>

    @Composable
    override fun registerPermission(isRecording: (Boolean) -> Unit) {
        permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (it) {
                initMediaRecorder(context) {
                    isRecording(it)
                }
                Toast.makeText(context, "Audio Permission Granted", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(context, "Audio Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun initMediaRecorder(context: Context, isRecording: (Boolean) -> Unit) {

        val fileName = System.currentTimeMillis().toString() + ".m4a"
        val fileDir = File(context.filesDir, "audio")
        if (!fileDir.exists()) fileDir.mkdir()
        file = File(fileDir, fileName)

        mediaRecorder = MediaRecorder()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder.setAudioEncodingBitRate(128000)
        mediaRecorder.setAudioSamplingRate(44100)
        mediaRecorder.setOutputFile(file.path)

        startRecording {
            isRecording(it)
        }

    }

    private fun startRecording(isRecording: (Boolean) -> Unit) {
        try {
            mediaRecorder.prepare()
            mediaRecorder.start()
            isRecording(true)
            //show audio view and layout
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopRecording(deleteRecording: Boolean = false, isRecording: (Boolean) -> Unit) {
        try {
            //stop showing audio view or layout
            mediaRecorder.stop()
            mediaRecorder.release()
            isRecording(false)
            //reset icons and composables
            if (deleteRecording)
                file.delete()
            else
                Log.d("JOENOTETAG", "Save audio to DB")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun startTimer(millisToFinish: Long) {
        timerJob?.cancel()
        timerJob = coroutineScope.countDownTimer(
            millisToFinish,
            50,
            onInterval = {
                msToFinishOnResume = it
                val text = it.milliSecondsToCountDown()
//                val payload = mapOf(
//                    Constant.Key.TIMER to text,
//                    Constant.Key.PROGRESS to getExoPlayerProgress(),
//                )
//                notesAdapter.updateAudioViewHolder(currentPosition, payload)
            },
            onFinished = {
                val text = exoPlayer.duration.milliSecondsToCountDown()
//                val payload = mapOf(
//                    Constant.Key.TIMER to text,
//                    Constant.Key.PROGRESS to 0f,
//                )
//                notesAdapter.updateAudioViewHolder(currentPosition, payload, true)
            })
    }

    fun pauseTimer() = timerJob?.cancel()

    fun resumeTimer() = startTimer(msToFinishOnResume)

    private fun getExoPlayerProgress(): Float =
        ((exoPlayer.currentPosition * 100) / exoPlayer.duration).toFloat()


    private fun CoroutineScope.countDownTimer(
        totalMillis: Long,
        intervalInMillis: Long = 1000,
        onInterval: (millisLeft: Long) -> Unit = {},
        onFinished: () -> Unit = {},
    ) = this.launch(Dispatchers.IO) {
        var total = totalMillis
        while (isActive) {
            if (total > 0) {
                withContext(Dispatchers.Main) {
                    onInterval(total)
                }
                delay(intervalInMillis)
                total -= intervalInMillis
            } else {
                withContext(Dispatchers.Main) {
                    onFinished()
                    cancel("Task Completed")
                }
            }
        }
    }

    private fun Long.milliSecondsToCountDown(): String {
        if (this <= 0) return "00:00"
        val seconds = this / 1000
        val hour = seconds / 3600
        val min = (seconds / 60) % 60
        val sec = seconds % 60
        val min0 = if (min < 10) "0" else ""
        val sec0 = if (sec < 10) "0" else ""
        val hourStr = when (hour) {
            0L -> ""
            in 1..10 -> "0$hour:"
            else -> "$hour:"
        }
        return "$hourStr$min0$min:$sec0$sec"
    }


    private var currentlyPlayingId: Long = -1
    private val metaDataRetriever = MediaMetadataRetriever()

    fun getCurrentlyPlayingId() = currentlyPlayingId

    fun updateCurrentPlayingId(id: Long) = id.also { currentlyPlayingId = it }

    fun updateAudioViewHolder(position: Int, payload: Map<*, *>, audioEnded: Boolean = false) {
        if (audioEnded) {
            updateCurrentPlayingId(-1)
//            notifyItemChanged(position)
        } else {
//            notifyItemChanged(position, payload)
        }
    }

    val getAudioLength: (String?) -> String = { filePath ->
        try {
            metaDataRetriever.setDataSource(filePath)
            val durationStr: String? =
                metaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            durationStr?.toLong()?.milliSecondsToCountDown() ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun playAudio(journalEntry: JournalEntry, context: Context) {
        if (this::exoPlayer.isInitialized)
            exoPlayer.stop()
        else {
            exoPlayer = ExoPlayer.Builder(context).build()
            exoPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    if (playbackState == ExoPlayer.STATE_READY)
                        startTimer(exoPlayer.duration)
                }
            })
        }

        val mediaItem = MediaItem.fromUri(journalEntry.audioFile)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
//            notesAdapter.updateCurrentPlayingId(note.id)

    }


    override fun startAudioProcess(scope: CoroutineScope, isRecording: (Boolean) -> Unit) {
        coroutineScope = scope
        requestAudioPermission(context, permissionLauncher) {
            isRecording(it)
        }

    }

    override fun stopAudioProcess(scope: CoroutineScope, isRecording: (Boolean) -> Unit) {
        scope.cancel("Audio Record Process completed")
        stopRecording {
            isRecording(it)
        }

    }


    private fun requestAudioPermission(
        context: Context,
        permsLauncher: ManagedActivityResultLauncher<String, Boolean>,
        isRecording: (Boolean) -> Unit
    ) {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(context, "Check Audio Permission GRANTED", Toast.LENGTH_SHORT).show()
                initMediaRecorder(context) {
                    isRecording(it)
                }
            }

            (context as ComponentActivity).shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                Toast.makeText(context, "Audio Permission Show Rationale", Toast.LENGTH_SHORT)
                    .show()
                permsLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }

            else -> {
                Toast.makeText(
                    context,
                    "Audio Check Permission DENIED, requesting ...",
                    Toast.LENGTH_SHORT
                ).show()
                permsLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

}




@Composable
fun AudioView() {
    Surface {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "Recording"
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "00:00:00",
                    fontSize = 30.sp
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterVertically)
                        .weight(1f)
                        .padding(6.dp)
                        .clickable { }
                ) {
                    Row(
                        modifier = Modifier
                            .height(50.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .align(Alignment.Center)
                    ) {
                        Icon(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Stop"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            text = "STOP"
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .height(70.dp)
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .padding(6.dp)
                        .clickable { }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(
                                shape = RoundedCornerShape(40.dp),
                                color = androidx.compose.ui.graphics.Color.Magenta,
                                width = 2.dp
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(color = androidx.compose.ui.graphics.Color.Magenta, shape = CircleShape)
                                .clip(CircleShape)
                                .align(Alignment.Center)
                        ) {}
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterVertically)
                        .weight(1f)
                        .padding(6.dp)
                        .clickable { }
                ) {
                    Row(
                        modifier = Modifier
                            .height(50.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .align(Alignment.Center)
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            text = "NEXT"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Next"
                        )
                    }
                }
            }
        }
    }
}






