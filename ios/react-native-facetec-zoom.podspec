require "json"
package = JSON.parse(File.read(File.join(__dir__, '../', 'package.json')))

Pod::Spec.new do |s|
  s.name          = package['name']
  s.version       = package["version"]
  s.summary       = package['description']
  s.requires_arc  = true
  s.license       = package["license"]
  s.homepage      = "https://github.com/jakubkoci/react-native-facetec-zoom.git#fix/native-dependencies"
  s.authors       = package["author"]
  s.source        = { :git => 'https://github.com/jakubkoci/react-native-facetec-zoom.git', :branch => 'fix/native-dependencies' }
  s.source_files = "*.{h,m,swift}"
  s.platform      = :ios, '7.0'
  s.dependency    'React'
  s.dependency    'ZoomAuthenticationHybrid'

end
