require "json"
package = JSON.parse(File.read(File.join(__dir__, '../', 'package.json')))

Pod::Spec.new do |s|
  s.name          = package['name']
  s.version       = package["version"]
  s.summary       = package['description']
  s.requires_arc  = true
  s.license       = package["license"]
  s.homepage      = "https://github.com/jakubkoci/react-native-facetec-zoom.git"
  s.authors       = package["author"]
  s.source        = { :git => 'https://github.com/petr-hlavnicka560/react-native-facetec-zoom.git', :branch => 'feature/sdk9' }
  s.source_files  = "**/*.{h,m,swift}"
  s.platform      = :ios, '7.0'
  s.dependency    'React'
  s.dependency    'FaceTecSDK'
  s.swift_version = '4.2'

end
