#pragma version(1)
#pragma rs java_package_name(ca.finlay.photowarp.app)

#define C_PI 3.141592653589793238462643383279502884197169399375
#include "rs_core.rsh"

const uchar4* input;
uchar4* output;
int width;
int height;

void swirl(float);
void bulge(float);
void fisheye(float);
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
			new_angle = angle + (factor * radius);


			srcX = (int)(floor(radius * cos(new_angle)+0.5f));
			srcY = (int)(floor(radius * sin(new_angle)+0.5f));
			srcX += cX;
			srcY += cY;
			srcY = height - srcY;

			setPixelAt(j, i, getPixelAt(srcX, srcY));
		}
	}
}

void bulge(float factor)
{
	float x, y;
	float r, a, rn, cX, cY;

	float xdist, ydist;
	float srcX, srcY;

	cX = (float) width / 2.0f;
   	cY = (float) height / 2.0f;

	for (y = 0; y < height; y++)
	{
		for (x = 0; x < width; x++)
		{
			xdist = ((float) x / (float)width);
			ydist = ((float) y / (float)height);

			r = sqrt(pow((xdist-0.5f), 2) + pow((ydist-0.5f), 2));
			a = atan2((xdist-0.5f), (ydist-0.5f));
			rn = pow(r,factor)/0.5f;

			srcX = rn * sin(a) + 0.5f;
			srcY = rn * cos(a) + 0.5f;
			srcX *= (float) width;
			srcY *= (float) height;

			setPixelAt(x, y, getPixelAt((int)srcX, (int)srcY));
		}
	}
}

void fisheye(float factor)
{
	float ny, nx, r, nr;
	float theta, nxn, nyn;
	int x, y, x2, y2;

	for (y = 0; y < height; y++)
	{
		ny = ((2.0f * y) / height) - 1.0f;
		for (x = 0; x < width; x++)
		{
			nx = ((2.0f * x) / width) - 1.0f;
			r = sqrt(nx*nx + ny*ny);

			if (0.0f <= r && r <= 1.0)
			{
				nr = (r + (1.0f - nr)) / factor;

				if (nr <= 1.0)
				{
                 	theta = atan2(ny, nx);
                 	nxn = nr * cos(theta);
                 	nyn = nr * sin(theta);

                 	x2 = (int) (((nxn+1)*width) / 2.0f);
                 	y2 = (int) (((nyn+1)*height) / 2.0f);

                 	setPixelAt(x, y, getPixelAt(x2, y2));

				}
			}
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


