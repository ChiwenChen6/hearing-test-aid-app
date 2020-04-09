package com.changwon.chiwen.frequency;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.Equalizer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * An activity that listens for audio and then uses a TensorFlow model to detect particular classes,
 * by default a small set of action words.
 */
public class SpeechActivity extends Activity
     {
  private processor_add mProcessor;


  // Constants that control the behavior of the recognition code and model
  // settings. See the audio recognition tutorial for a detailed explanation of
  // all these, but you should customize them to match your training settings if
  // you are running your own model.
  private static final int SAMPLE_RATE = 16384;
  public static final int FFT_SIZE = 4096;
  private static final int SAMPLE_DURATION_MS = 100000;
  private static final int RECORDING_LENGTH = 16384;
  private static final long AVERAGE_WINDOW_DURATION_MS = 1000;
  private static final float DETECTION_THRESHOLD = 0.50f;
  private static final int SUPPRESSION_MS = 1500;
  private static final int MINIMUM_COUNT = 3;
  private static final long MINIMUM_TIME_BETWEEN_SAMPLES_MS = 30;
  private static final String LABEL_FILENAME = "file:///android_asset/conv_actions_labels.txt";
  private static final String MODEL_FILENAME = "file:///android_asset/Gmodel_30.tflite";
  public LineChart lineChart;
  public LineChart lineChart2;
  public static final double DB_BASELINE = Math.pow(2, 15) * FFT_SIZE * Math.sqrt(2);

  private Equalizer mEqualizer;





  // UI elements.
  private static final int REQUEST_RECORD_AUDIO = 13;
  private static final String LOG_TAG = SpeechActivity.class.getSimpleName();

  // Working variables.
  short[] recordingBuffer = new short[RECORDING_LENGTH];
  short[] recordingBuffer2 = new short[RECORDING_LENGTH];
  int recordingOffset = 0;
  boolean shouldContinue = true;
  private Thread recordingThread;
  boolean shouldContinueRecognition = true;
  private Thread recognitionThread;
  private Thread fft;
  private Thread fft2;
  private Thread tfliterun;
  private final ReentrantLock recordingBufferLock = new ReentrantLock();

  private List<String> labels = new ArrayList<String>();
  private List<String> displayedLabels = new ArrayList<>();
  private RecognizeCommands recognizeCommands = null;
  private LinearLayout bottomSheetLayout;
  private LinearLayout gestureLayout;
  private BottomSheetBehavior sheetBehavior;
  private TextView modelLabel;
  private Button switchModel,StartSpeechBtn;
  private Interpreter tfLite;
  private ImageView bottomSheetArrowImageView;
  public static int[] RealLeftValue = new int[100];
  public static int[] RealRightValue = new int[100];



  private TextView yesTextView,
      noTextView,
      upTextView,
      downTextView,
      leftTextView,
      rightTextView,
      onTextView,
      offTextView,
      stopTextView,
      goTextView;
  private TextView sampleRateTextView, inferenceTimeTextView;
  private ImageView plusImageView, minusImageView;
  private SwitchCompat apiSwitchCompat;
  private TextView threadsTextView;
  private long lastProcessingTimeMs;
  private Handler handler = new Handler();
  private TextView selectedTextView = null;
  private HandlerThread backgroundThread;
  private Handler backgroundHandler;
  AudioTrack audioTrack;
  int playBufSize;
  private String modelName;
   String[] modelNames;

  /** Memory-map the model file in Assets. */
  private static MappedByteBuffer loadModelFile(AssetManager assets, String modelFilename)
      throws IOException {
    AssetFileDescriptor fileDescriptor = assets.openFd(modelFilename);
    FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
    FileChannel fileChannel = inputStream.getChannel();
    long startOffset = fileDescriptor.getStartOffset();
    long declaredLength = fileDescriptor.getDeclaredLength();
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // Set up the UI.
    RealLeftValue = MainTest.RealLeftValue;
    RealRightValue = MainTest.RealRightValue;

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_speech);
    lineChart = findViewById(R.id.line_chart);
    lineChart2 = findViewById(R.id.line_chart2);
    initChart();
    initChart2();
    modelLabel = (TextView) findViewById(R.id.model_label);
    switchModel = (Button) findViewById(R.id.switch_model);
    StartSpeechBtn= (Button) findViewById(R.id.StartSpeechBtn);

    try{
      modelNames = getAssets().list("models");
    } catch (IOException e){
      Toast.makeText(SpeechActivity.this,"models folder not found", Toast.LENGTH_SHORT).show();
    }
    //modelName = modelNames[0];
    //tfliteOptions.setNumThreads(numThreads);
    //modelLabel.setText(modelName.substring(0,modelName.length()-3));

    /*try {
        GpuDelegate delegate = new GpuDelegate();
        Interpreter.Options options = (new Interpreter.Options()).addDelegate(delegate);
        tflite = new Interpreter(loadModelFile(),options);
    } catch (IOException e) {
        e.printStackTrace();
    }*/
    switchModel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        StartSpeechBtn.setEnabled(true);
        stopRecording();
        switchModelDialog().show();
        Toast.makeText(SpeechActivity.this,"load succesed", Toast.LENGTH_SHORT).show();

      }
    });

    // Load the labels for the model, but only display those that don't start
    // with an underscore.

    String actualLabelFilename = LABEL_FILENAME.split("file:///android_asset/", -1)[1];
    Log.i(LOG_TAG, "Reading labels from: " + actualLabelFilename);
    BufferedReader br = null;
    try {
      br = new BufferedReader(new InputStreamReader(getAssets().open(actualLabelFilename)));
      String line;
      while ((line = br.readLine()) != null) {
        labels.add(line);
        if (line.charAt(0) != '_') {
          displayedLabels.add(line.substring(0, 1).toUpperCase() + line.substring(1));
        }
      }
      br.close();
    } catch (IOException e) {
      throw new RuntimeException("Problem reading label file!", e);
    }

    // Set up an object to smooth recognition results to increase accuracy.
    recognizeCommands =
        new RecognizeCommands(
            labels,
            AVERAGE_WINDOW_DURATION_MS,
            DETECTION_THRESHOLD,
            SUPPRESSION_MS,
            MINIMUM_COUNT,
            MINIMUM_TIME_BETWEEN_SAMPLES_MS);


    String actualModelFilename = MODEL_FILENAME.split("file:///android_asset/", -1)[1];

    if(modelName!=null)
      actualModelFilename=modelName;
    try {
      tfLite = new Interpreter(loadModelFile(getAssets(), actualModelFilename));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    tfLite.resizeInput(0, new int[] {RECORDING_LENGTH, 1});
    //tfLite.resizeInput(1, new int[] {1});

    // Start the recording and recognition threads.
    requestMicrophonePermission();
    StartSpeechBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startRecording();
        startRecognition();
        StartSpeechBtn.setEnabled(false);
      }

    });

    sampleRateTextView = findViewById(R.id.sample_rate);
    inferenceTimeTextView = findViewById(R.id.inference_info);
    bottomSheetLayout = findViewById(R.id.bottom_sheet_layout);
    gestureLayout = findViewById(R.id.gesture_layout);
    sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
    bottomSheetArrowImageView = findViewById(R.id.bottom_sheet_arrow);

    threadsTextView = findViewById(R.id.threads);
    plusImageView = findViewById(R.id.plus);
    minusImageView = findViewById(R.id.minus);
    apiSwitchCompat = findViewById(R.id.api_info_switch);

    /*yesTextView = findViewById(R.id.yes);
    noTextView = findViewById(R.id.no);
    upTextView = findViewById(R.id.up);
    downTextView = findViewById(R.id.down);
    leftTextView = findViewById(R.id.left);
    rightTextView = findViewById(R.id.right);

    stopTextView = findViewById(R.id.stop);
    goTextView = findViewById(R.id.go);*/

    apiSwitchCompat.setOnCheckedChangeListener(this::onCheckedChanged);

    ViewTreeObserver vto = gestureLayout.getViewTreeObserver();
    vto.addOnGlobalLayoutListener(
        new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override
          public void onGlobalLayout() {
            gestureLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            int height = gestureLayout.getMeasuredHeight();

            sheetBehavior.setPeekHeight(height);
          }
        });
    sheetBehavior.setHideable(false);

    sheetBehavior.setBottomSheetCallback(
        new BottomSheetBehavior.BottomSheetCallback() {
          @Override
          public void onStateChanged(@NonNull View bottomSheet, int newState) {
            switch (newState) {
              
              case BottomSheetBehavior.STATE_HIDDEN:
                break;
              case BottomSheetBehavior.STATE_EXPANDED:
                {
                  bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_down);
                }
                break;
              case BottomSheetBehavior.STATE_COLLAPSED:
                {
                  bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_up);
                }
                break;
              case BottomSheetBehavior.STATE_DRAGGING:
                break;
              case BottomSheetBehavior.STATE_SETTLING:
                bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_up);
                break;
            }
          }

          @Override
          public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });

    plusImageView.setOnClickListener(this::onClick);
    minusImageView.setOnClickListener(this::onClick);

    sampleRateTextView.setText(SAMPLE_RATE + " Hz");
  }

  private void requestMicrophonePermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      requestPermissions(
          new String[] {android.Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
    }
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, String[] permissions, int[] grantResults) {
    if (requestCode == REQUEST_RECORD_AUDIO
        && grantResults.length > 0
        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      //startRecording();
      //startRecognition();
    }
  }

  public synchronized void startRecording() {
    if (recordingThread != null) {
      return;
    }
    shouldContinue = true;
    recordingThread =
        new Thread(
            new Runnable() {
              @Override
              public void run() {
                record();
              }
            });
    recordingThread.start();
  }

  public synchronized void stopRecording() {
    if (recordingThread == null) {
      return;
    }
    shouldContinue = false;
    recordingThread = null;
  }

  private void record() {
    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

    // Estimate the buffer size we'll need for this device.
     int bufferSize =
        AudioRecord.getMinBufferSize(
            SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
    if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
      bufferSize = SAMPLE_RATE * 2;
    }
    final int BufferSize =bufferSize;
    short[] audioBuffer = new short[bufferSize / 2];

    AudioRecord record =
        new AudioRecord(
            MediaRecorder.AudioSource.DEFAULT,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize);

    if (record.getState() != AudioRecord.STATE_INITIALIZED) {
      Log.e(LOG_TAG, "Audio Record can't initialize!");
      return;
    }

    record.startRecording();

    Log.v(LOG_TAG, "Start recording");

    // Loop, gathering audio data and copying it to a round-robin buffer.
    while (shouldContinue) {

      int numberRead = record.read(audioBuffer, 0, audioBuffer.length);
      int maxLength = recordingBuffer.length;
      int newRecordingOffset = recordingOffset + numberRead;
      int secondCopyLength = Math.max(0, newRecordingOffset - maxLength);
      int firstCopyLength = numberRead - secondCopyLength;



      recordingBufferLock.lock();

      try {
        System.arraycopy(audioBuffer, 0, recordingBuffer, recordingOffset, firstCopyLength);
        System.arraycopy(audioBuffer, firstCopyLength, recordingBuffer, 0, secondCopyLength);
        recordingOffset = newRecordingOffset % maxLength;
      } finally {
        recordingBufferLock.unlock();
      }
      /*fft = new Thread(new Runnable() {
        @Override
        public void run() {
           //short[] shortData = toLittleEndian(recordingBuffer,BufferSize);
          double[] fftData = fastFourierTransform(recordingBuffer);
          final double[] decibelFrequencySpectrum = computePowerSpectrum(fftData);
          handler.post(new Runnable() {
            @Override
            public void run() {
              System.out.println("setData " );

              setData(decibelFrequencySpectrum);

            }

          });
        }

      });
      fft.start();*/
    }


    record.stop();
    record.release();
  }

  public synchronized void startRecognition() {
    if (recognitionThread != null) {
      return;
    }
    shouldContinueRecognition = true;
    recognitionThread =
        new Thread(
            new Runnable() {
              @Override
              public void run() {
                recognize();
              }
            });
    recognitionThread.start();
  }

  public synchronized void stopRecognition() {
    if (recognitionThread == null) {
      return;
    }
    shouldContinueRecognition = false;
    recognitionThread = null;
  }

  private void recognize() {



    Log.v(LOG_TAG, "Start recognition");

    short[] inputBuffer = new short[RECORDING_LENGTH];
    short[] outputBuffer = new short[RECORDING_LENGTH];
    short[] outputBuffer2 = new short[RECORDING_LENGTH];

    float[][] floatInputBuffer = new float[RECORDING_LENGTH][1];
    float[][] floatOutputBuffer = new float[RECORDING_LENGTH][1];
    float[][] outputScores = new float[1][labels.size()];
    int[] sampleRateList = new int[] {SAMPLE_RATE};
    playBufSize = AudioTrack.getMinBufferSize(16384, AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT);
    System.out.println("BUFFER SIZE VALUE IS " +  playBufSize );
    AudioManager audioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);

    String framesPerBuffer = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
    int framesPerBufferInt = Integer.parseInt(framesPerBuffer);
    if (framesPerBufferInt == 0) framesPerBufferInt = 256; // Use default

    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,100,AudioManager.FLAG_SHOW_UI);

    audioTrack = new AudioTrack(audioManager.STREAM_MUSIC,16834,AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT, playBufSize, AudioTrack.MODE_STREAM);


    // Loop, grabbing recorded data and running the recognition model on it.
    while (shouldContinueRecognition) {
      long startTime = new Date().getTime();

      recordingBufferLock.lock();
      try {
        int maxLength = recordingBuffer.length;
        int firstCopyLength = maxLength - recordingOffset;
        int secondCopyLength = recordingOffset;
        System.arraycopy(recordingBuffer, recordingOffset, inputBuffer, 0, firstCopyLength);
        System.arraycopy(recordingBuffer, 0, inputBuffer, firstCopyLength, secondCopyLength);
        processData(inputBuffer);

      } finally {
        recordingBufferLock.unlock();
      }
      float[][][] floatOutputBufferThr= new float[1][16384][1];
      float[][][] a= new float[1][16384][1];

      /*for (int i = 0; i < RECORDING_LENGTH; ++i) {
        floatInputBuffer[i][0] = inputBuffer[i] / 32767.0f;
      }*/
      processData(inputBuffer);

      for (int i = 0; i < RECORDING_LENGTH; ++i) {
          a[0][i][0] = inputBuffer[i]/ 32767.0f *2;
      }
        System.out.println(a.length);



        int b=floatInputBuffer.length;
        System.out.println(b);
        //Toast.makeText(SpeechActivity.this, a, Toast.LENGTH_SHORT).show();


        /*for (int i = 0; i < RECORDING_LENGTH; ++i) {
            floatInputBuffer[i][0] = inputBuffer[i] / 32767.0f;
        }*/


      Object[] inputArray = {floatInputBuffer, sampleRateList};
      /*
      Map<Integer, Object> outputMap = new HashMap<>();
      outputMap.put(0, outputScores);*/

      // Run the model.
      //tfLite.runForMultipleInputsOutputs(inputArray, outputMap);

        //tfLite.getInputTensor(1);
      tfliterun  = new Thread(new Runnable() {
        @Override
        public void run() {
          try{tfLite.run(a,floatOutputBufferThr);System.out.println("成功運行");}
          catch(StringIndexOutOfBoundsException e){
            System.out.println("模型出錯");}
          for (int i = 0; i < RECORDING_LENGTH; ++i) {
            outputBuffer[i] = (short)Math.floor(floatOutputBufferThr[0][i][0]* 32767.0f);
            outputBuffer2[i]=outputBuffer[i];
            if(outputBuffer[i]<350)
              outputBuffer[i]=0;

          }


          processData(outputBuffer);
          processData(outputBuffer);
        }
      });
      tfliterun.start();


      recordingBufferLock.lock();
      try {
        int maxLength = recordingBuffer.length;
        int firstCopyLength = maxLength - recordingOffset;
        int secondCopyLength = recordingOffset;
        System.arraycopy(recordingBuffer, recordingOffset, outputBuffer, 0, firstCopyLength);
        System.arraycopy(recordingBuffer, 0, outputBuffer, firstCopyLength, secondCopyLength);
        processData(outputBuffer);

      } finally {
        recordingBufferLock.unlock();
      }

      /*fft2 = new Thread(new Runnable() {
        @Override
        public void run() {
          //short[] shortData = toLittleEndian(recordingBuffer,BufferSize);
          double[] fftData2 = fastFourierTransform(outputBuffer2);


          final double[] decibelFrequencySpectrum2 = computePowerSpectrum(fftData2);
          andler.post(new Runnable() {
            @Override
            public void run() {
              System.out.println("setData2 " );

              setData2(decibelFrequencySpectrum2);

            }

          });
        }

      });
      fft2.start();*/
      mEqualizer = new Equalizer(0,  audioTrack.getAudioSessionId());
      // 啟用均衡控制效果
      mEqualizer.setEnabled(true);
      short brands = mEqualizer.getNumberOfBands();
      final short minEQLevel = mEqualizer.getBandLevelRange()[0];//第一個下標為最低的限度範圍
      short maxEQLevel = mEqualizer.getBandLevelRange()[1];  // 第二個下標為最高的限度範圍
      int  EQLevel= maxEQLevel-minEQLevel;
      for (short i = 0; i < brands; i++)
      {
        // 設定該頻率的均衡值
        mEqualizer.setBandLevel((short)0,
                (short) ((minEQLevel+ (maxEQLevel- minEQLevel)/2)+2000)
        );
        // 設定該頻率的均衡值
        mEqualizer.setBandLevel((short)1,
                (short) ((minEQLevel+ (maxEQLevel- minEQLevel)/2)+2000)
        );

        // 設定該頻率的均衡值
        mEqualizer.setBandLevel((short)2,
                (short) ((minEQLevel+ (maxEQLevel- minEQLevel)/2))
        );

        // 設定該頻率的均衡值
        mEqualizer.setBandLevel((short)3,
                (short) ((minEQLevel+ (maxEQLevel- minEQLevel)/2))
        );

        // 設定該頻率的均衡值
        mEqualizer.setBandLevel((short)4,
                (short) ((minEQLevel+ (maxEQLevel- minEQLevel)/2)+1000)
        );


      }



      audioTrack.play();//开始播放
      audioTrack.write(outputBuffer, 0, RECORDING_LENGTH);



      // Use the smoother to figure out if we've had a real recognition event.
      long currentTime = System.currentTimeMillis();
      final RecognizeCommands.RecognitionResult result =
          recognizeCommands.processLatestResults(outputScores[0], currentTime);
      lastProcessingTimeMs = new Date().getTime() - startTime;
      runOnUiThread(
          new Runnable() {
            @Override
            public void run() {

            }
          });

    }

    Log.v(LOG_TAG, "End recognition");
  }


  public void onClick(View v) {
    if (v.getId() == R.id.plus) {
      String threads = threadsTextView.getText().toString().trim();
      int numThreads = Integer.parseInt(threads);
      numThreads++;
      threadsTextView.setText(String.valueOf(numThreads));
      //            tfLite.setNumThreads(numThreads);
      int finalNumThreads = numThreads;
      backgroundHandler.post(() -> tfLite.setNumThreads(finalNumThreads));
    } else if (v.getId() == R.id.minus) {
      String threads = threadsTextView.getText().toString().trim();
      int numThreads = Integer.parseInt(threads);
      if (numThreads == 1) {
        return;
      }
      numThreads--;
      threadsTextView.setText(String.valueOf(numThreads));
      tfLite.setNumThreads(numThreads);
      int finalNumThreads = numThreads;
      backgroundHandler.post(() -> tfLite.setNumThreads(finalNumThreads));
    }
  }
  /**
   * 初始化降噪
   */
  private void initProccesor() {
    mProcessor = new processor_add();
    mProcessor.init(SAMPLE_RATE);
  }

  /**
   * 釋放降噪資源
   */
  private void releaseProcessor() {
    if (mProcessor != null) {
      mProcessor.release();
    }
  }



  /**
   * 處理需要降噪的音訊資料
   *
   * @param data
   */
  private void processData(short[] data) {
    if (mProcessor != null) {
      mProcessor.processNoise(data);
    }
  }

   public void stop() {
     tfliterun = null;
   }
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    backgroundHandler.post(() -> tfLite.setUseNNAPI(isChecked));
    if (isChecked) apiSwitchCompat.setText("NNAPI");
    else apiSwitchCompat.setText("TFLITE");
  }

  private static final String HANDLE_THREAD_NAME = "CameraBackground";

  private void startBackgroundThread() {
    backgroundThread = new HandlerThread(HANDLE_THREAD_NAME);
    backgroundThread.start();
    backgroundHandler = new Handler(backgroundThread.getLooper());
  }

  private void stopBackgroundThread() {
    backgroundThread.quitSafely();
    try {
      backgroundThread.join();
      backgroundThread = null;
      backgroundHandler = null;
    } catch (InterruptedException e) {
      Log.e("amlan", "Interrupted when stopping background thread", e);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();

    startBackgroundThread();
  }

   @Override
  protected void onStop() {
    super.onStop();
    stopBackgroundThread();
  }

  public void initChart() {

    lineChart.setTouchEnabled(true);
    lineChart.setDragEnabled(false);

    // Grid背景色
    lineChart.setDrawGridBackground(true);

    // no description text
    lineChart.getDescription().setEnabled(true);

    lineChart.setBackgroundColor(Color.LTGRAY);

    LineData data = new LineData();
    data.setValueTextColor(Color.BLACK);

    // add empty data
    lineChart.setData(data);

    //        // Grid
    XAxis xAxis = lineChart.getXAxis();
    xAxis.setAxisMaximum(2048);
    xAxis.setAxisMinimum(0);
    xAxis.enableGridDashedLine(10f, 10f, 0f);
    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

    YAxis leftAxis = lineChart.getAxisLeft();
    // Y軸最大最小設定
    leftAxis.setAxisMaximum(0f);
    leftAxis.setAxisMinimum(-150f);

    leftAxis.enableGridDashedLine(10f, 10f, 0f);
    leftAxis.setDrawZeroLine(true);

    lineChart.getAxisRight().setEnabled(false);
  }
  public void initChart2() {

    lineChart.setTouchEnabled(true);
    lineChart.setDragEnabled(false);

    // Grid背景色
    lineChart2.setDrawGridBackground(true);

    // no description text
    lineChart2.getDescription().setEnabled(true);

    lineChart2.setBackgroundColor(Color.LTGRAY);

    LineData data = new LineData();
    data.setValueTextColor(Color.BLACK);

    // add empty data
    lineChart2.setData(data);

    //        // Grid
    XAxis xAxis = lineChart2.getXAxis();
    xAxis.setAxisMaximum(2048);
    xAxis.setAxisMinimum(0);
    xAxis.enableGridDashedLine(10f, 10f, 0f);
    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

    YAxis leftAxis = lineChart2.getAxisLeft();
    // Y軸最大最小設定
    leftAxis.setAxisMaximum(0f);
    leftAxis.setAxisMinimum(-150f);

    leftAxis.enableGridDashedLine(10f, 10f, 0f);
    leftAxis.setDrawZeroLine(true);

    lineChart2.getAxisRight().setEnabled(false);
  }

  public short[] toLittleEndian(byte[] buf, int bufferSize) {


    ByteBuffer bf = ByteBuffer.wrap(buf);
    bf.clear();
    bf.order(ByteOrder.LITTLE_ENDIAN);
    short[] shortData = new short[bufferSize / 2];

    for (int i = bf.position(); i < bf.capacity() / 2; i++) {

      shortData[i] = bf.getShort();
    }
    return shortData;
  }


  public double[] fastFourierTransform(short[] shortData) {

    FFT4g fft = new FFT4g(FFT_SIZE);
    double[] fftData = new double[FFT_SIZE];
    for(int i = 0; i < FFT_SIZE; i++) {
      fftData[i] = (double) shortData[i];
    }
    fft.rdft(1, fftData);

    return fftData;
  }


  public double[] computePowerSpectrum(double[] fftData) {

    double[] powerSpectrum = new double[FFT_SIZE / 2];
    //DeciBel Frequency Spectrum
    double[] decibelFrequencySpectrum = new double[FFT_SIZE / 2];
    for(int i = 0; i < FFT_SIZE; i += 2) {
      //dbfs[i / 2] = (int) (20 * Math.log10(Math.sqrt(Math.pow(FFTdata[i], 2) + Math.pow(FFTdata[i + 1], 2)) / dB_baseline));
      powerSpectrum[i / 2] = Math.sqrt(Math.pow(fftData[i], 2) + Math.pow(fftData[i + 1], 2));
      decibelFrequencySpectrum[i / 2] = (int) (20 * Math.log10(powerSpectrum[i / 2] / DB_BASELINE));
    }
    return decibelFrequencySpectrum;
  }
  public synchronized double[] IIRFilter(double[] signal, double[] a, double[] b) {

    double[] in = new double[b.length];
    double[] out = new double[a.length-1];

    double[] outData = new double[signal.length];

    for (int i = 0; i < signal.length; i++) {

      System.arraycopy(in, 0, in, 1, in.length - 1);
      in[0] = signal[i];

      //calculate y based on a and b coefficients
      //and in and out.
      float y = 0;
      for(int j = 0 ; j < b.length ; j++){
        y += b[j] * in[j];

      }

      for(int j = 0;j < a.length-1;j++){
        y -= a[j+1] * out[j];
      }

      //shift the out array
      System.arraycopy(out, 0, out, 1, out.length - 1);
      out[0] = y;

      outData[i] = y;


    }
    return outData;
  }


  public void setData(double[] data) {

    ArrayList<Entry> values = new ArrayList<>();

    for (int i = 0; i < data.length; i++) {
      values.add(new Entry(i, (int)data[i], null, null));
    }

    LineDataSet set1;

    if (lineChart.getData() != null &&
            lineChart.getData().getDataSetCount() > 0) {

      set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
      set1.setValues(values);
      lineChart.getData().notifyDataChanged();
      lineChart.notifyDataSetChanged(); // let the chart know it's data changed
      lineChart.invalidate(); // refresh
    } else {
      // create a dataset and give it a type
      set1 = new LineDataSet(values, "Spectrum");

      set1.setDrawIcons(false);
      set1.setColor(Color.rgb(0, 0, 240));
      set1. setDrawCircles(false);
      //set1.setCircleColor(Color.BLACK);
      set1.setLineWidth(0.5f);
      //set1.setCircleRadius(0.25f);
      //set1.setDrawCircleHole(false);
      set1.setValueTextSize(0f);
      set1.setDrawFilled(false);
      set1.setFormLineWidth(1f);
      set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
      set1.setFormSize(15.f);
      set1.setDrawValues(true);

      //set1.setFillColor(Color.rgb(10, 10, 240));

      ArrayList<ILineDataSet> dataSets = new ArrayList<>();
      dataSets.add(set1); // add the datasets

      // create a data object with the datasets
      LineData lineData = new LineData(dataSets);

      // set data
      lineChart.setData(lineData);
    }
  }
  public void setData2(double[] data) {

    ArrayList<Entry> values = new ArrayList<>();

    for (int i = 0; i < data.length; i++) {
      values.add(new Entry(i, (int)data[i], null, null));
    }

    LineDataSet set1;

    if (lineChart2.getData() != null &&
            lineChart2.getData().getDataSetCount() > 0) {

      set1 = (LineDataSet) lineChart2.getData().getDataSetByIndex(0);
      set1.setValues(values);
      lineChart2.getData().notifyDataChanged();
      lineChart2.notifyDataSetChanged(); // let the chart know it's data changed
      lineChart2.invalidate(); // refresh
    } else {
      // create a dataset and give it a type
      set1 = new LineDataSet(values, "Spectrum");

      set1.setDrawIcons(false);
      set1.setColor(Color.rgb(0, 0, 240));
      set1. setDrawCircles(false);
      //set1.setCircleColor(Color.BLACK);
      set1.setLineWidth(0.5f);
      //set1.setCircleRadius(0.25f);
      //set1.setDrawCircleHole(false);
      set1.setValueTextSize(0f);
      set1.setDrawFilled(false);
      set1.setFormLineWidth(1f);
      set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
      set1.setFormSize(15.f);
      set1.setDrawValues(true);

      //set1.setFillColor(Color.rgb(10, 10, 240));

      ArrayList<ILineDataSet> dataSets = new ArrayList<>();
      dataSets.add(set1); // add the datasets

      // create a data object with the datasets
      LineData lineData = new LineData(dataSets);

      // set data
      lineChart2.setData(lineData);
    }
  }


   public Dialog switchModelDialog() {
     AlertDialog.Builder builder = new AlertDialog.Builder(SpeechActivity.this);
     builder.setTitle("Choose a Model")
             .setItems(modelNames, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int which) {
                 //modelName = modelNames[which];
                 //tfHelper = new TensorFlowInferenceInterface(getAssets(), "models/" + modelName);
                 modelLabel.setText(modelName.substring(0,modelName.length()-3));
                 modelName = modelNames[which];
                 //tflite = new Interpreter("models/" + modelName);
                 //catch (IOException e) {
                 //   e.printStackTrace();
                 //}
                 modelLabel.setText(modelName.substring(0,modelName.length()-3));

               }
             });
     return builder.create();
   }
}

