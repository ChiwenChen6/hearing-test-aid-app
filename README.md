
hearingtest and hearing aid use tflite model
===
#Requirements    <br>
Win10, Nvidia1070ti, i7-8700    <br>
*	Anaconda    <br>
*	PyTorch    <br>
conda install pytorch torchvision -c pytorch    <br>
*	librosa    <br>
pip install librosa    <br>
*	tqdm    <br>
conda install tqdm    <br>
#Datasets    <br>
>The datasets must build in 16kHz.    <br>
We used two dataset to train the default model.    <br>

>>https://datashare.is.ed.ac.uk/handle/10283/2791    <br>
The database was designed to train and test speech enhancement methods that operate at 48kHz.    <br>
    <br>
[1] Valentini-Botinhao, Cassia. (2017). Noisy speech database for training speech enhancement algorithms and TTS models, 2016 [sound]. University of Edinburgh. School of Informatics. Centre for Speech Technology Research (CSTR).    <br>
    <br>
>>https://zenodo.org/record/260228    <br>
NTCD-TIMIT: A New Database and Baseline for Noise-robust Audio-visual Speech Recognition    <br>
    <br>
[2] Abdelaziz, Ahmed Hussen. "NTCD-TIMIT: A New Database and Baseline for Noise-Robust Audio-Visual Speech Recognition." INTERSPEECH. pp. 3752-3756. 2017.    <br>

#Parts of usefull code for data prepare 
Prepare data for training and testing the various models.     <br>
If user want to use the second dataset NTCD-TIMIT, there are some useful matlab program.     <br>

*	readfileandsave.m    <br>
To read wav files of NTCD-TIMIT and save in new name by different volume.    <br>
*	readfilename.m    <br>
To save the names of the wav files in folder. Users must change the test.txt and triain.txt by themselves.     <br>
*	Downsampling.m    <br>
Re-write the wav file in 16kHz.    <br>
*	python prepare_data.py    <br>
The folder path may be edited if you keep the database in a different folder. This script is to be executed only once and the all the models reads from the same location.    <br>
*	run_wgan-gp_se.py : GAN model with Wassterstein loss and Gradient Penalty    <br>

#parameter:    <br>
TEST_SEGAN:  True (Testset also add in training)     <br>
False(disable testing)    <br>

WGAN-gp (default settings):    <br>

BATCH_SIZE = 100    <br>
D learning rate = 4e-4    <br>
G learning rate = 4e-4    <br>
Epoch=80    <br>
L1 loss =500    <br>    <br>
Leaky relu alpha=0.3    <br>

Data preprocess:    <br>
Pre Emphasis = 0.95    <br>
Write in Float32    <br>
Stride factor= 0.5    <br>

trained model(rmsprop): <br>
https://drive.google.com/open?id=1EYoOj-fpD5ELBbNgVWqF0ooeqZY9l11O
