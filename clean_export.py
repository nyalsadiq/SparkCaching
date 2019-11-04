import csv
in_file = open("./export.csv", "rb")
reader = csv.reader(in_file)
out_file = open("./out.csv", "wb")
writer = csv.writer(out_file)
for row in reader:
    for i in range(0, len(row)):
        if row[i] is None or row[i] == "":
            row[i] = "0"

    split = row[3].split(",")
    for i in range(0, len(split)):
        split[i] = "\'" + split[i].strip() + "\'"
    row[3] = ",".join(split)
    row[3] = "[" + row[3] + "]"

    split = row[4].split(",")
    for i in range(0, len(split)):
        split[i] = "\'" + split[i].strip() + "\'"

    row[4] = ",".join(split)
    row[4] = "[" + row[4] + "]"

    row[11] = "\'" + row[11] + "\'"
    writer.writerow(row)

in_file.close()    
out_file.close()