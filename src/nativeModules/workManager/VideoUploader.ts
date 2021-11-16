import {NativeModules} from 'react-native';
const {WorkManager} = NativeModules;

interface IWorkManager {
  enqueueVideoUploads(videoUris: string[]): void;
}

export default WorkManager as IWorkManager;
