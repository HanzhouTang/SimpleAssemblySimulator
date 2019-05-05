  ; refer from "modern x86 assembly"
.data 
FibVals_    byte 0, 1, 1, 2, 3, 5, 8, 13
        
NumFibVals_ byte ($ - FibVals_) / sizeof byte

FibMem_     byte (FibVals_)
.code

; extern "C" int CalcSum_(int a, int b, int c)
;
; Description:  This function demonstrates passing arguments between
;               a C++ function and an assembly language function.
;
; Returns:      a + b + c

add_ proc
        mov ebp,esp
loop:
        mov ebp, 123 
        mov ebp, -123 
        mov ebp, [eax]
        mov ebp, [1+eax]
        mov ebp, [NumFibVals_+eax]
        ;mov ebp, [loop+eax]
        mov ebp, [eax*4+ebx+1024]
        mov ebp, [eax*4+ebx-1024]
        mov ebp, [-1024+eax*4+ebx]
        mov ebp, [1024+eax*4+ebx]
        mov eax, [ebp*1]
        jmp loop 
add_ endp
end add_
