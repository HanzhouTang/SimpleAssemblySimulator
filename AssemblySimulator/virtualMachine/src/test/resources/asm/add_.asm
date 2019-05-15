


.data 
FibVals     dword 0, 1, 1, 2, 3, 5, 8, 13
            dword 21, 34, 55, 89, 144, 233, 377, 610 

.code 
add_ proc
        mov eax, [28]
        mov ecx, [28]
        add eax,ecx                         ; eax = 13 + 13
add_ endp
end add_

; maybe later, I need make one operand can have more than one displacement. 
