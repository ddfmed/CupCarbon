loop
wait
read x
send $x 3
if ($x==a)
mark 1
else
mark 0
endif