export function capitalize(str: string) {
    return str.charAt(0).toUpperCase() + str.substr(1);
}

// export interface IJsonMetaData<T> {
//     name?: string,
//     clazz?: {new(): T}
// }
//
// export function getClazz(target: any, propertyKey: string): any{
//     return Reflect.getMetadata("design:type", target, propertyKey)
// }
// export function getJsonProperty<T>(target: any, propertyKey: string):  IJsonMetaData<T> {
//     return Reflect.getMetadata(jsonMetadataKey, target, propertyKey);
// }
//
// export default class MapUtils {
//     static isPrimitive(obj: any) {
//         switch (typeof obj) {
//             case "string":
//             case "number":
//             case "boolean":
//                 return true;
//         }
//         return (obj instanceof String || obj === String ||
//         obj instanceof Number || obj === Number ||
//         obj instanceof Boolean || obj === Boolean);
//     }
//
//     static isArray(object: any) {
//         if (object === Array) {
//             return true;
//         } else if (typeof Array.isArray === "function") {
//             return Array.isArray(object);
//         }
//         else {
//             return (object instanceof Array);
//         }
//     }
//
//     static deserialize<T>(clazz: {new(): T}, jsonObject: any) {
//         if ((clazz === undefined) || (jsonObject === undefined)) return undefined;
//         let obj = new clazz();
//         Object.keys(obj).forEach((key) => {
//             let propertyMetadataFn: (metadata: IJsonMetaData<T>) => any = (propertyMetadata) => {
//                 let propertyName = propertyMetadata.name || key;
//                 let innerJson = jsonObject ? jsonObject[propertyName] : undefined;
//                 let clazz = getClazz(obj, key);
//                 if (MapUtils.isArray(clazz)) {
//                     let metadata = getJsonProperty(obj, key);
//                     if (metadata.clazz || MapUtils.isPrimitive(clazz)) {
//                         if (innerJson && MapUtils.isArray(innerJson)) {
//                             return innerJson.map(
//                                 (item) => MapUtils.deserialize(metadata.clazz, item)
//                             );
//                         } else {
//                             return undefined;
//                         }
//                     } else {
//                         return innerJson;
//                     }
//
//                 } else if (!MapUtils.isPrimitive(clazz)) {
//                     return MapUtils.deserialize(clazz, innerJson);
//                 } else {
//                     return jsonObject ? jsonObject[propertyName] : undefined;
//                 }
//             };
//
//             let propertyMetadata = getJsonProperty(obj, key);
//             if (propertyMetadata) {
//                 obj[key] = propertyMetadataFn(propertyMetadata);
//             } else {
//                 if (jsonObject && jsonObject[key] !== undefined) {
//                     obj[key] = jsonObject[key];
//                 }
//             }
//         });
//         return obj;
//     }
// }