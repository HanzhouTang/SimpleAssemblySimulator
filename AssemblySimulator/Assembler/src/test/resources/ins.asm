  ; refer from "modern x86 assembly"
    ; refer from "modern x86 assembly"
.data 
FibVals_    byte 0, 1, 1, 2, 3, 5, 8, 13
        
NumFibVals_ byte ($ - FibVals_) / sizeof byte

FibMem_     byte (FibVals_)
.code

.code

; extern "C" int CalcSum_(int a, int b, int c)
;
; Description:  This function demonstrates passing arguments between
;               a C++ function and an assembly language function.
;
; Returns:      a + b + c

add_ proc
        mov ebp,esp
add_ endp
end add_
