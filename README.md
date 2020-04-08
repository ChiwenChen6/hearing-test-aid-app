Requirements
Win10, Nvidia1070ti, i7-8700
•	Anaconda
•	PyTorch
conda install pytorch torchvision -c pytorch
•	librosa
pip install librosa
•	tqdm
conda install tqdm
Datasets
The datasets must build in 16kHz.
We used two dataset to train the default model.
https://datashare.is.ed.ac.uk/handle/10283/2791
The database was designed to train and test speech enhancement methods that operate at 48kHz.

[1] Valentini-Botinhao, Cassia. (2017). Noisy speech database for training speech enhancement algorithms and TTS models, 2016 [sound]. University of Edinburgh. School of Informatics. Centre for Speech Technology Research (CSTR).

https://zenodo.org/record/260228
NTCD-TIMIT: A New Database and Baseline for Noise-robust Audio-visual Speech Recognition

[2] Abdelaziz, Ahmed Hussen. "NTCD-TIMIT: A New Database and Baseline for Noise-Robust Audio-Visual Speech Recognition." INTERSPEECH. pp. 3752-3756. 2017.


Prepare data for training and testing the various models. 
If user want to use the second dataset NTCD-TIMIT, there are some useful matlab program. 

readfileandsave.m
To read wav files of NTCD-TIMIT and save in new name by different volume.
readfilename.m
To save the names of the wav files in folder. Users must change the test.txt and triain.txt by themselves. 
Downsampling.m
Re-write the wav file in 16kHz.
python prepare_data.py
The folder path may be edited if you keep the database in a different folder. This script is to be executed only once and the all the models reads from the same location.
run_wgan-gp_se.py : GAN model with Wassterstein loss and Gradient Penalty

parameter:
TEST_SEGAN:  True (Testset also add in training) 
False(disable testing)

WGAN-gp (default settings):

BATCH_SIZE = 100
D learning rate = 4e-4
G learning rate = 4e-4
Epoch=80
L1 loss =500
Leaky relu alpha=0.3

Data preprocess:
Pre Emphasis = 0.95
Write in Float32
Stride factor= 0.5

trained model(rmsprop) 
https://drive.google.com/open?id=1EYoOj-fpD5ELBbNgVWqF0ooeqZY9l11O
