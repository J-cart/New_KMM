package com.prototype.newkmm

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.ExoPlayer
import com.prototype.newkmm.presentation.screen.RecordingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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



actual interface MainAudioUtil {

    actual val recordingState: StateFlow<RecordingState>

    @Composable
    actual fun registerPermission(isRecording: (Boolean) -> Unit)

    @Composable
    actual fun RecordScreenView(
        isAudioRecording: Boolean,
        onStartRec: () -> Unit,
        onStopRec: (String?, Boolean) -> Unit,
        onNavigateUp: () -> Unit
    )

    actual fun startAudioProcess(scope: CoroutineScope, resume: Boolean, pause: Boolean, isRecording: (Boolean) -> Unit)
    actual fun stopAudioProcess(scope: CoroutineScope, stop: Boolean, isRecording: (Boolean) -> Unit)
}


class AudioUtil(private val context: Context) : MainAudioUtil {
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var file: File
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private lateinit var exoPlayer: ExoPlayer

    private lateinit var permissionLauncher: ManagedActivityResultLauncher<String, Boolean>

    private val _recordingState = MutableStateFlow<RecordingState>(RecordingState.Idle)

    override val recordingState: StateFlow<RecordingState> = _recordingState.asStateFlow()


    @Composable
    override fun registerPermission(isRecording: (Boolean) -> Unit) {
        permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (it) {
                initMediaRecorder(context){state->
                    isRecording(state)
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

        startRecording{
            isRecording(it)
        }
    }

    private fun startRecording(isRecording: (Boolean) -> Unit) {
        try {
            mediaRecorder.prepare()
            mediaRecorder.start()
            _recordingState.value = RecordingState.Start
            isRecording(true)
            //show audio view and layout
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopRecording(deleteRecording: Boolean = false,isRecording: (Boolean) -> Unit) {
        try {
            //stop showing audio view or layout
            mediaRecorder.stop()
            mediaRecorder.release()
            isRecording(false)
            //reset icons and composables
            if (deleteRecording) {
                file.delete()
                _recordingState.value = RecordingState.Stop(null)
            }
            else {
                Log.d("PROTOTYPEKMM", "Save audio to DB")
                _recordingState.value = RecordingState.Stop(file.absolutePath)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    override fun startAudioProcess(scope: CoroutineScope, resume: Boolean, pause: Boolean,isRecording: (Boolean) -> Unit) {
        if (pause) {
            mediaRecorder.pause()
            _recordingState.value = RecordingState.Pause
            return
        }

        if (resume) {
            mediaRecorder.resume()
            _recordingState.value = RecordingState.Resume
            return
        }

//        coroutineScope = scope
        requestAudioPermission(context, permissionLauncher){
            isRecording(it)
        }

    }

    override fun stopAudioProcess(scope: CoroutineScope, stop: Boolean,isRecording: (Boolean) -> Unit) {

        stopRecording(stop){
            isRecording(it)
        }
        coroutineScope.cancel("Audio Record Process completed")

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
                initMediaRecorder(context){
                    isRecording(it)
                }
                Toast.makeText(context, "Check Audio Permission GRANTED", Toast.LENGTH_SHORT).show()

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

    @Composable
    override fun RecordScreenView(
        isAudioRecording: Boolean,
        onStartRec: () -> Unit,
        onStopRec: (String?,Boolean) -> Unit,
        onNavigateUp:()->Unit
    ) {

        Surface {
            Column(modifier = Modifier.fillMaxSize()) {
                IconButton(
                    modifier = Modifier.align(Alignment.Start)
                        .padding(start = 16.dp, top = 20.dp, bottom = 12.dp), onClick = {
                        onNavigateUp()
                    }) {
                    Icon(imageVector = Icons.Default.ArrowBackIos, contentDescription = "Back")
                }
                Column(
                    modifier = Modifier
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
                            .clickable {
                                onStopRec(null, true)
                                onNavigateUp()
                            }
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
                            .clickable {

                                if(isAudioRecording){
                                    onStartRec()
                                }else{
                                    onStopRec("file.absolutePath",false)
                                }
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(
                                    shape = RoundedCornerShape(40.dp),
                                    color = Color.Magenta,
                                    width = 2.dp
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isAudioRecording) {
                                Text("PAUSE/STOP")
                            }else {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(color = Color.Magenta, shape = CircleShape)
                                        .clip(CircleShape)
                                        .align(Alignment.Center)
                                ) {}
                            }

                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterVertically)
                            .weight(1f)
                            .padding(6.dp)
                            .clickable {
                                onNavigateUp()
                            }
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


}







