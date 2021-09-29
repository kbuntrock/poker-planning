export class VoteValue {
  value: number;
  red: number;
  green: number;
  blue: number;

  constructor(value: number) {
    this.value = value;
  }

  getColor() {
    return 'rgb('+this.red+','+this.green+','+this.blue+')';
  }

  // input: hue: in [0,360] and saturation,value in [0,100] - output: r,g,b in [0,255]
  private hsv2rgb(hue:number, saturation:number, value:number)
  {
    const h = hue;
    const s = saturation / 100;
    const v = value / 100;
    let f= (n,k=(n+h/60)%6) => v - v*s*Math.max( Math.min(k,4-k,1), 0);
    return [Math.round((f(5)*255)),
            Math.round((f(3)*255)),
            Math.round((f(1)*255))];
  }

  setColor(percentage:number, fromHue:number, toHue:number, fromSaturation:number, toSaturation:number, fromValue:number, toValue:number) {
    const hue:number = ((toHue - fromHue) * percentage) + fromHue;
    const saturation:number = ((toSaturation - fromSaturation) * percentage) + fromSaturation;
    const value:number = ((toValue - fromValue) * percentage) + fromValue;

    const resultHSV = this.hsv2rgb(hue, saturation, value);

    this.red = resultHSV[0];
    this.green = resultHSV[1];
    this.blue = resultHSV[2];
  }
}
