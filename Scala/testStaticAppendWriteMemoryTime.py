import os
import time


start = time.time()

for x in range(32):
    start = time.time()
    os.system("scala StaticBufferCommands < TestFiles/bigbible_append_then_write.txt")
    print(str(time.time() - start) + " seconds")

os.system("rm bigbibleoutput.txt")
