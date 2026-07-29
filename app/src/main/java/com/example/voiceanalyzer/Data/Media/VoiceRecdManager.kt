package com.example.voiceanalyzer.Data.Media

import android.content.ContentValues
import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VoiceRecdManager(contex: Context) {

    private var record : MediaRecorder?=null
    lateinit var pdff : ParcelFileDescriptor
    lateinit var recoriguri : Uri
    private var stime =0L
    var timeerjob: Job?=null

    var _rectime = MutableStateFlow(0L)
    var  rectime : StateFlow<Long> = _rectime
    private var paustime =0L





    // Add these to VoiceRecdManager
    private val _amplitudes = MutableStateFlow<List<Float>>(List(80) { 0f })
    val am: StateFlow<List<Float>> = _amplitudes

// Add this inside strttime(), in the while(true) loop:




    fun createRecodUri(contex: Context): Uri {
        val values = ContentValues().apply {

            put(
                MediaStore.Audio.Media.DISPLAY_NAME,
                "Audio_${System.currentTimeMillis()}.m4a")
            put(MediaStore.Audio.Media.MIME_TYPE,"audio/mp4")
            put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC + "/VoiceRecorder")
            put(MediaStore.Audio.Media.IS_PENDING,1)
        }
        return contex.contentResolver.insert(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,values
        )!!

    }
    fun strttime(){
        timeerjob?.cancel()
        timeerjob = CoroutineScope(Dispatchers.Main).launch {
            while (true){
                val ela = paustime + (System.currentTimeMillis()- stime)
                _rectime.value = ela
                delay(100L)
                if (record != null) {
                    val raw = getVisualizerAmplitude(true)
                    val normalized = (raw.toFloat() / 32767f).coerceIn(0f, 1f)
                    val current = _amplitudes.value.toMutableList()
                    if (current.size >= 80) current.removeAt(0)
                    current.add(normalized)
                    _amplitudes.value = current
                }

            }
        }
    }
    fun paus(){
        record?.pause()
        paustime += System.currentTimeMillis() - stime
        timeerjob?.cancel()


    }

    fun resme(){
        record?.resume()
        stime = System.currentTimeMillis()
        strttime()

    }

    fun strtrecod (contex: Context){
        stime = System.currentTimeMillis()
        recoriguri = createRecodUri(contex)
        pdff = contex.contentResolver.openFileDescriptor(recoriguri,"w")!!
        record = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(pdff.fileDescriptor)
            prepare()
            start()
        }
        strttime()

    }
    fun stoprecod(contex: Context){
        record?.stop()
        record?.release()
        pdff.close()
        timeerjob?.cancel()
        val values = ContentValues().apply {

            put(MediaStore.Audio.Media.IS_PENDING,0)
        }
         contex.contentResolver.update(
            recoriguri,values,null,null
        )
        _rectime.value= 0L
        _amplitudes.value = List(80) { 0f }
        Toast.makeText(contex,"THE Audio has been stored ", Toast.LENGTH_LONG).show()
    }

    fun dismiss(contex: Context){
        try {
            record?.apply {
                stop()
                reset()
                release()

            }
        }
        catch (e: Exception){

        }
        finally {
            if (::recoriguri.isInitialized){

                contex.contentResolver.delete(recoriguri,null,null)
            }
            reset()
        }
    }

    fun reset(){
        record=null;
        timeerjob?.cancel()

        stime =0L
        paustime =0L
        _rectime.value= 0L

    }
    fun getVisualizerAmplitude(isRecording: Boolean): Int {
        if (!isRecording) return 0
        return try {
            record?.maxAmplitude ?: 0
        } catch (e: IllegalStateException) {
            0
        }
    }




}