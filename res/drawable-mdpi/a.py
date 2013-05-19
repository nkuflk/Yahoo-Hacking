import Image

s = "weibo.jpg"

img = Image.open(s)

w , h = img.size

small = img.resize((w*5/7,h*5/7),Image.ANTIALIAS)
small.save(s)
