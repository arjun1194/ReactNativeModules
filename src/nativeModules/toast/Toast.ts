import {NativeModules} from 'react-native';
const {Toast} = NativeModules;

interface IToast {
  /** show a Android Native Toast
   * @param message The Text you want to show in the Toast Message
   */
  showToast(message: string): void;
}

export default Toast as IToast;
