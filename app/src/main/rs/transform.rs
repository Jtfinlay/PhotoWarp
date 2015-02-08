#pragma version(1)
#pragma rs java_package_name(ca.finlay.photowarp.app)

#define C_PI 3.141592653589793238462643383279502884197169399375
#include "rs_core.rsh"

const uchar4* input;
uchar4* output;
int width;
int height;

void swirl(float);
static uchar4 getPixelAt(int, int);
void setPixelAt(int, int, uchar4);


void swirl(float factor)
{

	int i, j, srcX, srcY;
	float relX, relY, cX, cY;
	float angle, new_angle, radius;

	cX = (float) width / 2.0f;
	cY = (float) height / 2.0f;

	for(i = 0; i < height; i++)
	{
		relY = cY-i;

		for(j = 0; j < width; j++)
		{
			relX = j - cX;
			if (relX != 0)
			{
				angle = atan( fabs(relY) / fabs(relX));
				if (relX > 0 && relY < 0) angle = 2.0f * C_PI - angle;
				else if (relX <= 0 && relY >= 0) angle = C_PI - angle;
				else if (relX <=0 && relY < 0) angle += C_PI;
			}
			else
			{
				if (relY >= 0) angle = 0.5f * C_PI;
				else angle = 1.5f * C_PI;
			}

			radius = sqrt( relX*relX + relY*relY);
//			new_angle = angle + 1.0f /(factor * radius + (4.0f/C_PI));
			new_angle = angle + (factor * radius);

			//rsDebug("angle, new_angle", angle, new_angle);

			srcX = (int)(floor(radius * cos(new_angle)+0.5f));
			srcY = (int)(floor(radius * sin(new_angle)+0.5f));
			srcX += cX;
			srcY += cY;
			srcY = height - srcY;

			//rsDebug("Differences", (srcX-j), (srcY-i));
			setPixelAt(j, i, getPixelAt(srcX, srcY));
		}
	}
}

void bulge()
{
	int x, y, newX, newY;
	float r, a, rn, relX, relY;

	for (y = 0; y < height; y++)
	{
		relY = (height / 2.0f);
		for (x = 0; x < width; x++)
		{
			relX = (width / 2.0f);

			r = sqrt( relX * relX + relY * relY );
			a = atan( fabs(relY) / fabs(relX));

			rn = pow(r, 2.5f) / 0.5f;

			newX = (int) (rn * cos(a) + relX);
			newY = (int) (rn * sin(a) + relY);

            setPixelAt(newX, newY, getPixelAt(x,y));
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


