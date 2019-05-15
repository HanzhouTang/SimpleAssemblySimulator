.data 
FibVals     dword 0, 1, 1, 2, 3, 5, 8, 13
Test        dword 21, 34, 55, 89, 144, 233, 377, 610 
.code 
test proc
mov eax, [Test]
mov ecx, 45 
add eax, ecx 
mov edx, 123
mul eax, edx 
mov ecx, 10 
div eax, ecx 
test endp 
end test





