.data 
FibVals_    byte 0, 1, 1, 2, 3, 5, 8, 13
        
NumFibVals_ byte ($ - FibVals_) / sizeof byte

FibMem_     byte (FibVals_)
