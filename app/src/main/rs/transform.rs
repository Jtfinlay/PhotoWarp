#pragma version(1)
#pragma rs java_package_name(ca.finlay.photowarp.app)

#define C_PI 3.141592653589793238462643383279502884197169399375

const uchar4* input;
uchar4* output;
int width;
int height;

void XXX(int, int);
static uchar4 getPixelAt(int, int);
void setPixelAt(int, int, uchar4);


void XXX(int x, int y) {

	int i, j;


	for(j = 0; j < height; j++) {
		for(i = 0; i < width; i++) {
			setPixelAt(i, j, getPixelAt(i, j));
		}
	}
}



//a convenience method to clamp getting pixels into the image
static uchar4 getPixelAt(int x, int y) {
	if(y>=height) y = height-1;
	if(y<0) y = 0;
	if(x>=width) x = width-1;
	if(x<0) x = 0;
	return input[y*width + x];
}

//take care of setting x,y on the 1d-array representing the bitmap
void setPixelAt(int x, int y, uchar4 pixel) {
	output[y*width + x] = pixel;
}


