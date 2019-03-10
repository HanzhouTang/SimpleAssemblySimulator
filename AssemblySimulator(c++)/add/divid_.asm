.model flat, c
	.code 
divid_ proc 
	push ebp
	mov ebp, esp 
	push ebx 
	xor eax,eax
	mov ecx,[ebp+8]
	mov ebx,[ebp+12]
	or ebx,ebx
	jz InvalidDivisor 
	mov eax, ecx 
	cdq ;extends eax to edx:eax 
	idiv dword ptr ebx
	mov ebx, [ebp+16]
	mov [ebx], eax
	mov ebx, [ebp+20]
	mov [ebx], edx
	mov eax, 1 
	InvalidDivisor:
	pop ebx
	pop ebp 
	ret
divid_ endp
	end 
