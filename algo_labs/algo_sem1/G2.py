t = int(input())
for i in range(t):
    string = input().split()
    impostors = int(string[0])
    crewmates = int(string[1])
    count = 0
    progression = (2 * impostors - (impostors - 1)) / 2 * impostors
    if progression >= crewmates:
        print("Impostors")
        left = 0
        right = impostors
        mid = (left + right) // 2
        while right - left > 1:
            mid = (left + right) // 2
            if ((2 * impostors - (mid - 1)) / 2 * mid) < crewmates:
                left = mid
            else:
                right = mid
        for j in range(left, impostors + 1):
            if ((2 * impostors - (j - 1)) / 2 * j) >= crewmates:
                print(j)
                break
    else:
        print("Crewmates")
        print(impostors)