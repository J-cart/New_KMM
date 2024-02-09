package com.prototype.newkmm.presentation.screen

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.touchlab.kermit.Logger
import com.prototype.newkmm.PlatformUtil
import com.prototype.newkmm.domain.JournyEntry
import com.prototype.newkmm.domain.JournyEntryDataSource
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Composable
fun RecordScreen(
    onNavigateUp: () -> Unit,
    platformUtil: PlatformUtil,
    journyEntryDataSource: JournyEntryDataSource
) {

    val recordViewModel =
        getViewModel(key = Unit, viewModelFactory { RecordScreenViewModel(journyEntryDataSource) })

    val scope = rememberCoroutineScope()

    var isRecordingMode by remember { mutableStateOf(false) }
    val audioUtil = platformUtil.createAudioUtil()
    audioUtil.registerPermission {
        isRecordingMode = it
    }
    val recordingState by audioUtil.recordingState.collectAsState()

  /*  LaunchedEffect(recordingState){
        Logger.d("PROTOTYPEKMM"){
            "Recording State-> $recordingState"
        }
        if (recordingState is RecordingState.Stop){
            (recordingState as RecordingState.Stop).audioPath?.let {
                recordViewModel.addRecording(
                    JournyEntry(
                        uuid = Clock.System.now().toEpochMilliseconds().toString(),
                        audioFile = it,
                        createdAt = Clock.System.now().toEpochMilliseconds(),
                        updatedAt = Clock.System.now().toEpochMilliseconds()
                    )
                )
            } ?: onNavigateUp()

        }
    }*/
    LaunchedEffect(isRecordingMode){
        Logger.d("PROTOTYPEKMM"){
            "Recording State-> $isRecordingMode"
        }

    }
/* Platform RecordView

    audioUtil.RecordScreenView(
        isAudioRecording = isRecordingMode,
        onStartRec = {
            audioUtil.startAudioProcess(scope,false, false){
                isRecordingMode = it
            }
        },
        onStopRec = {path,stop->
            audioUtil.stopAudioProcess(scope,stop){
                isRecordingMode = it
            }
        },
        onNavigateUp= {
            onNavigateUp()
        }
    )

*/

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
                            audioUtil.stopAudioProcess(scope, stop = true){
                                isRecordingMode = it
                            }
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
                            /*if(recordingState is RecordingState.Start){
                                audioUtil.stopAudioProcess(scope,false){
                                    isRecordingMode = it
                                }
                            }else{
                                audioUtil.startAudioProcess(scope,resume = false, pause = false){
                                    isRecordingMode = it
                                }
                            }*/
                            if(isRecordingMode){
                                audioUtil.stopAudioProcess(scope,false){
                                    isRecordingMode = it
                                }
                            }else{
                                audioUtil.startAudioProcess(scope,resume = false, pause = false){
                                    isRecordingMode = it
                                }
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
                        /*if (recordingState is RecordingState.Start) {
                            Text("PAUSE/STOP")
                        }else {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(color = Color.Magenta, shape = CircleShape)
                                    .clip(CircleShape)
                                    .align(Alignment.Center)
                            ) {}
                        }*/
                        if (isRecordingMode) {
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


class RecordScreenViewModel(private val journyEntryDataSource: JournyEntryDataSource) :
    ViewModel() {

    var recordingState = MutableStateFlow<RecordingState>(RecordingState.Idle)
        private set

    fun updateRecordingState(state: RecordingState) {
        recordingState.value = state
    }

    fun addRecording(journyEntry: JournyEntry) {
        viewModelScope.launch {
            journyEntryDataSource.getAllJournalEntry().collect {
                if (it.isEmpty()) {
                    journyEntryDataSource.insertJournyEntry(journyEntry.copy(title = "journy #1"))
                } else {
                    journyEntryDataSource.insertJournyEntry(journyEntry.copy(title = "journy #${it.size + 1}"))
                }
            }

        }
    }

}

sealed class RecordingState {
    object Idle : RecordingState()
    object Start : RecordingState()
    data class Stop(val audioPath: String?) : RecordingState()
    object Pause : RecordingState()
    object Resume : RecordingState()
}