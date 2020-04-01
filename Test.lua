-- Lua functions can return multiple results, cool
function maximum (a)
	local maxIdx = 1
	local maxVal = a[maxIdx]
	for i, val in ipairs(a) do
		if val > maxVal then
			maxIdx = i
			maxVal = val
		end
	end
	return maxVal, maxIdx
end

print(maximum({4,5,2,1})) --> returns 5	2